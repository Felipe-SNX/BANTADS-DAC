const { Router } = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');

const router = Router();
const gerentesServiceUrl = 'http://localhost:5003';

router.get('/relatorio-especial', (req, res) => {
    //Lógica temporária que usa JSON SERVER
    console.log('Gerando relatório especial de clientes no Gateway...');
    res.json({
        message: 'Este é um relatório especial gerado pelo próprio API Gateway.',
        timestamp: new Date().toISOString(),
        geradoPor: 'Gateway Service',
    });
});

const gerentesProxy = createProxyMiddleware({
    target: gerentesServiceUrl,
    changeOrigin: true,
});

router.use('/', gerentesProxy);

module.exports = router;