// src/utils/errorHandler.js
const errorHandler = (error, req, res, next) => {
  console.error(error.stack);
  const statusCode = error.status || 500;
  res.status(statusCode).json({
    error: {
      message: error.message || 'Ocorreu um erro interno no servidor.',
    },
  });
};

module.exports = errorHandler;