const { Router } = require('express');

// Importa os roteadores de cada domínio
const clientesRoutes = require('./clientes.routes.js');
const contasRoutes = require('./contas.routes.js');
const gerentesRoutes = require('./gerentes.routes.js');

const router = Router();

router.use('/clientes', clientesRoutes);
router.use('/contas', contasRoutes);
router.use('/gerentes', gerentesRoutes);

module.exports = router;