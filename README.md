El sistema debe almacenar informacion sobre los clientes, transacciones

funcionalidades para un empleado
- accesi a un menu que permita visualizar las transacciones realizadas
- capacidad para reponer la cantidad de dinero en el cajero

funcionalidades usuario
- iniciar sesion
- revisar saldo
- revisar movimientos
- realizar depositos
- realizar retiros
- realizar transferencias

Entonces el usuario debe tener
- id
- nroCuenta (hasheada)
- saldo
- usuario
- contraseña
- rol (empleado/cliente)

las transacciones van a tener 
- nroCuenta
- tipo de transaccion
- fechaCreacion
- en caso de ser de tipo transferencia va a tener un nroCuentaReceptora

El cajero debera tener un saldo

APLICAR TEST