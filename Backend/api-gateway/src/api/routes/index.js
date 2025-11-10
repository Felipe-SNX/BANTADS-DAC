const { Router } = require('express');
const router = Router();

const authRoutes = require('./auth.routes');
const adminRoutes = require('./admin.routes');
const clienteRoutes = require('./clientes.routes');
const gerenteRoutes = require('./gerente.routes');
const contaRoutes = require('./contas.routes');

router.use('/clientes', clienteRoutes); 
router.use('/gerentes', gerenteRoutes);
router.use('/contas', contaRoutes);
router.use('/', authRoutes);
router.use('/', adminRoutes); 

module.exports = router;