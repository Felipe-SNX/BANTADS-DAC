// Arquivo: /src/routes/conta.routes.js
const { Router } = require('express');
const { contasServiceProxy } = require('../../proxies');
const { verifyToken } = require('../../middlewares/auth.middleware');
const router = Router();

router.use('/', verifyToken, contasServiceProxy);

module.exports = router;