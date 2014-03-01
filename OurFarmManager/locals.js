exports.setLocals = function(req, res, next){ //<- middleware function
  res.locals.user = req.session ? req.session.user : '';
  res.locals.yang = req.yang ? req.yang:'123';
  next();
}