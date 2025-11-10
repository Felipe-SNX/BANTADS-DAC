//Carrega as variáveis de ambiente do arquivo .env para process.env
require('dotenv').config();

const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const morgan = require('morgan');

const routes = require('./src/api/routes');
const errorHandler = require('./src/utils/errorHandler');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(helmet());
app.use(cors());
app.use(morgan('dev'));

app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(cors());

app.use('', routes);

//Rota para verificar se a API está funcionando
app.get('/health', (req, res) => {
    res.status(200).send('OK');
})

app.use((req, res, next) => {
    const error = new Error('Rota não encontrada');
    error.status = 404;
    next(error);
})

app.use(errorHandler);

app.listen(PORT, () => {
    console.log(`API Gateway rodando na porta ${PORT}`);
});

