
const jwt = require('jsonwebtoken');

const verifyToken = (req, res, next) => {
    const authHeader = req.headers['authorization'];

    if (!authHeader) {
        return res.status(403).json({ message: 'Acesso negado. Nenhum token foi fornecido.' });
    }

    const token = authHeader.split(' ')[1];

    if (!token) {
        return res.status(403).json({ message: 'Acesso negado. O token está mal formatado.' });
    }

    try {
        const decodedPayload = jwt.verify(token, process.env.JWT_SECRET);
        req.user = decodedPayload;
        next();

    } catch (error) {
        console.error("Erro de verificação de token:", error.message);
        return res.status(401).json({ message: 'Token inválido ou expirado. Faça login novamente.' });
    }
};

module.exports = verifyToken;