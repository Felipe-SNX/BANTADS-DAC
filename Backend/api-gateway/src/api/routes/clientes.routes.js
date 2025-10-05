const { Router } = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');

const router = Router();
const clientesServiceUrl = 'http://localhost:5001';

router.get('/relatorio-especial', (req, res) => {
    //Lógica temporária que usa JSON SERVER
    console.log('Gerando relatório especial de clientes no Gateway...');
    res.json({
        message: 'Este é um relatório especial gerado pelo próprio API Gateway.',
        timestamp: new Date().toISOString(),
        geradoPor: 'Gateway Service',
    });
});

const clientesProxy = createProxyMiddleware({
    target: clientesServiceUrl,
    changeOrigin: true,
});

router.use('/', clientesProxy);

module.exports = router;