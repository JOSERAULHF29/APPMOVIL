//importamos librerias 
const expres=require("express")
const mysql2=require("mysql2")
const body=require("body-parser")
const cron = require("node-cron");

//importamos la libreria de firebase 
const admin=require("firebase-admin")


const app = expres()
const puerto=5000
app.use(body.json());


//importamos el archivos json donde la clave privda 
const serviceAccount = require("C:/clave1.json");

//accedo a la llave 
admin.initializeApp({
   
    credential:admin.credential.cert(serviceAccount)
})
///creamos la conexion a bd
const conexion= mysql2.createConnection(
    {
    
        host:'localhost',
        database:'perroandroid',
        user:'root',
        password:'123456',
        port:3306

    }
)
conexion.connect(error=>{
     
    if(error) throw error.message
    console.log("conexion exitosa a BD")

})
//se levanta el puerto 
app.listen(puerto,()=>{

         console.log("conexion al servidor exitoso :" + puerto)
})

//// metododo para automatizar crear de usuario en firebase de manera rapida
async function migrarDatos() {
    try {
        const consulta = "SELECT * FROM cliente"; // Consulta para obtener todos los clientes
        conexion.query(consulta, async (error, rpta) => {
            if (error) {
                throw error.message;
            }

            for (let i = 0; i < rpta.length; i++) {
                const cliente = rpta[i];
                const email = cliente.Correo;
                const password = cliente.Contraseña;
                const nombre = cliente.Nombre;
                const mascota = cliente.Mascota;

                try {
                    // Crear el usuario en Firebase Authentication
                    const userRecord = await admin.auth().createUser({
                        email: email,
                        password: password
                    });

                    console.log('Usuario creado en Firebase:', userRecord.uid);
                    
                } catch (error) {
                    console.log("Error al crear usuario en Firebase:", error);
                }
            }
        });
    } catch (error) {
        console.error("Error en la migración:", error);
    }
}

// Programar la migración para que se ejecute automáticamente cada segundo 
cron.schedule('* * * * * *', () => {
    console.log('Iniciando migración de datos...');
    migrarDatos();  // Llama a la función de migración
});



///el servidro
app.get("/",(req,res)=>{


    res.send(" hola soy el backend ")
})
///mostramos la lista de clientes

app.get("/cliente",(req,res)=>{

       const consulta="SELECT * FROM cliente"
       conexion.query(consulta,(error,rpta)=>{
            
           if(error) throw error.message
           const obj={}
           if(rpta.length>0){
            obj.lista=rpta
               res.json(obj)
           }
            else
              {
                res.send("no hay registro ")

              }
       })
})

//la lista de categoria 

app.get("/categoria",(req,res)=>{

    const consulta= "SELECT * FROM categoria"
    conexion.query(consulta,(error,rpta)=>{
        if(error) throw error.message
        const obj={}
        if(rpta.length>0){
            obj.lista=rpta
            res.json(rpta)
        }
        else{
            res.send("no hay datos ")
        }
    })
})
//buscamos el cliende mediante el id 
app.get("/cliente/:id",(req,res)=>{
        const id=req.params.id;
         const consulta="SELECT * FROM cliente where idCliente=?"
         conexion.query(consulta,[id],(error,rpta)=>{
          
            if(error)  
               return console.error(error)

            if(rpta.length>0)
            {
                res.json(rpta[0])
            }
            else{
                res.send(" no hay datos ")
            }
         })
})
//obtenemos la lista de producto

app.get("/producto",(req,res)=>{

       const consulta="SELECT * FROM producto"
       conexion.query(consulta,(error,rpta)=>{

       if(error)  throw error.message
       const obj={}
       if(rpta.length>0)
       {
          obj.lista=rpta
          res.json(rpta)
       }
       else{
        res.send(" NO HAY DATOS ")
       }


       })



})
/// obtnemos lista de carrito

app.get("/carrito",(req,res)=>{

    const sql="SELECT * FROM carrito"
    conexion.query(sql,(error,rpta)=>{
      
         if(error) throw error.message
         if(rpta.length>0)
         {
            res.json(rpta)
         }
         else{

            res.send("NO HAY DATOS EN CARRITO")
         }
         

    })

})
///obtenemos el idcliente registrado o logeago 

app.get("/obtener", (req, res) => {
    
    const { Correo, Contraseña } = req.query;  // req.query permite nos  obtener parámetros de la URL

    // Consulta para verificar el email y la contraseña en la base de datos
    const query = 'SELECT idCliente FROM cliente WHERE Correo = ? AND Contraseña = ?';
    
    conexion.query(query, [Correo, Contraseña], (err, results) => {
        if (err) {
           
            return res.json('Error al consultar la base de datos');
        }

        if (results.length > 0) {
            // Si se encontró un cliente con esas credenciales, devolver el idCliente
            const idCliente = results[0].idCliente;
            return res.json({ idCliente });  // Retorna el idCliente en formato JSON
        } else {
        
            return res.json('Credenciales incorrectas');
        }
    });
});
//obtnemos los prodcutos para insertarlo en carrito de compra en el recyview

app.get("/carrito/:idCliente", async (req, res) => {
    const idCliente = req.params.idCliente; // Capturamos el ID del cliente desde la ruta
    try {
        // Usamos await para esperar la ejecución de la consulta
        const [rows] = await conexion.promise().execute(
            `
SELECT carrito.idcarrito, carrito.idCliente, carrito.idProducto, carrito.cantidad, 
       producto.nombre, producto.precio, producto.imagen 
FROM carrito 
JOIN producto ON carrito.idProducto = producto.idProducto 
WHERE carrito.idCliente = ?`,
            [idCliente]
        );
        if (rows.length === 0) {
            return res.json( "No se encontraron productos en el carrito para este cliente.");
        }
        res.json(rows);
    } catch (error) {
        console.error("Error al obtener el carrito:", error);
        
    }
});








// insertamos los productos en la tabla carrito 
app.post('/carrito', (req, res) => {
    const { idProducto,idCliente,cantidad} = req.body;

    // Verificar si el producto ya está en el carrito
    const check = 'SELECT * FROM carrito WHERE idProducto =? AND idCliente=?';
    conexion.query(check, [idProducto,idCliente], (err, results) => {
        if (err) {
            res.json('Error al verificar el carrito');
            return;
        }

        if (results.length > 0) {
            // Si existe, incrementar la cantidad
            const consulta = 'UPDATE carrito SET cantidad = cantidad + 1 WHERE  idProducto =? AND idCliente=?';
            conexion.query(consulta, [idProducto,idCliente], (err) => {
                if (err) {
                    res.json('Error al actualizar el carrito');
                } else {
                    res.json('Cantidad incrementada');
                }
            });
        } else {
            // Si no existe, agregarlo al carrito
            const insertQuery = 'INSERT INTO carrito (idProducto,cantidad,estado,idCliente) VALUES (?,1,1,?)';
          conexion.query(insertQuery, [idProducto,idCliente,cantidad], (err) => {
                if (err) {
                    res.json('Error al agregar al carrito');
                } else {
                    res.json('Producto agregado al carrito');
                }
            });
        }
    });});
//para el boton agregar cantidad "+"
    app.put('/carrito/incrementar', (req, res) => {
        const { idProducto,idCliente } = req.body;
    
        const query = 'UPDATE carrito SET cantidad = cantidad + 1 WHERE idProducto = ? AND idCliente= ?';
    conexion.query(query, [idProducto,idCliente], (err) => {
            if (err) {
                res.json('Error al incrementar la cantidad');
            } else {
                res.json('Cantidad incrementada');
            }
        });
    });
    //para dismunir para el boton dismunir:"-"

    app.put('/carrito/disminuir', (req, res) => {
        const { idProducto,idCliente } = req.body;
    
    const query = 'UPDATE carrito SET cantidad = cantidad - 1 WHERE idProducto = ? AND cantidad >0 AND idCliente=?';
        conexion.query(query, [idProducto,idCliente], (err) => {
            if (err) {
                res.json('Error al disminuir la cantidad');
            } else {
                res.json('Cantidad disminuida');
            }
        });
    });










///crear cliente nuevo
app.post("/cliente/nuevo",(req,res)=>{

    const cliente = {
        Nombre: req.body.Nombre,
        Correo: req.body.Correo,
        Contraseña: req.body.Contraseña,
        Mascota: req.body.Mascota
      };
    const consulta= "INSERT INTO cliente SET ?";
    conexion.query(consulta,cliente,(error)=>{

           if(error) return console.error(error.message)
           res.json(" Felicitaciones ,  usuario  creado  ")


      })
   })
//insertar datos a venta de carrito :
app.post("/venta/nuevo", (req, res) => {
    const { idCliente } = req.body; // Asegúrate de que esté recibiendo el idCliente correctamente.

    if (!idCliente) {
        return res.status(400).json({ error: "El idCliente es obligatorio." });
    }

    // Tu consulta SQL sigue siendo válida
    const consultaVenta = `
        INSERT INTO venta(idCliente, Monto, estado)
SELECT 
    c.idCliente,
    CAST(SUM(p.precio * c.cantidad) + (0.10 * SUM(p.precio * c.cantidad)) + (0.15 * SUM(p.precio * c.cantidad)) AS DECIMAL(10,2)) AS Monto,
    0
FROM carrito c 
JOIN producto p ON c.idProducto = p.idProducto
WHERE c.idCliente =?
GROUP BY c.idCliente;
    `;
    
    conexion.query(consultaVenta, [idCliente], (error,result) => {
        if (error) {
            console.error("Error en la consulta:", error);
            return res.status(500).json({ error: "Error al registrar la venta" });
        }
        const idventa = result.insertId; 
        res.json(`${idventa}`);
    });
});


//eliminar producto de carrito 
app.delete("/carrito/eliminar/:idCliente/:idProducto",(req,res)=>{

    const { idCliente, idProducto } = req.params;
  const sql="DELETE FROM carrito where idCliente=? AND idProducto=?"
  conexion.query(sql,[idCliente,idProducto],(error,rpta)=>{

    if(error)
    {
        console.error("error al eliminar",error)
    }
    else{
        if(rpta.affectedRows>0)
        {
            res.json(" producto eliminado exitosamente")
        }
      
    }

  })
})
//obtner el id de venta 
app.get("/VENTA/:idventa", (req, res) => {
    const { idventa } = req.params;
    const consulta = "SELECT * FROM venta WHERE idventa=?";
    
    conexion.query(consulta, [idventa], (error, rpta) => {
        if (error) {
            console.error("Error al conectarse", error);
            return res.status(500).json({ error: "Error en la consulta" }); // Respuesta en caso de error en la consulta
        }
        
        if (rpta.length > 0) {
            // Si se encuentra la venta, devuelve el primer resultado
            res.json(rpta[0]);
        } else {
            // Si no se encuentra la venta, devuelve un mensaje de error en formato JSON
            res.json({ error: "No se encontraron datos para este idventa" });
        }
    });
});









