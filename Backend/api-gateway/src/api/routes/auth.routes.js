const { Router } = require('express');
const { verifyToken } = require('../../middlewares/auth.middleware');
const { authServiceProxy } = require('../../proxies');
const router = Router();

router.post('/login', (req, res, next) => {
    req.url = '/auth/login';
    authServiceProxy(req, res, next);
});

router.post('/logout', verifyToken, (req, res, next) => {
    req.url = '/auth/logout';
    authServiceProxy(req, res, next);
});

router.get('/:email', (req, res, next) => {
    const emailDoUsuario = req.params.email;
    req.url = `/auth/${emailDoUsuario}`;
    authServiceProxy(req, res, next);
});

module.exports = router;