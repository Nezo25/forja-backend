const fs = require('fs');
let code = fs.readFileSync('src/main/java/com/pokeprint/api/controller/PokemonModelController.java', 'utf8');

code = code.replace(
  'model.setImageUrl(request.imageUrl());',
  `model.setImageUrl(request.imageUrl());
        model.setBasePrice(request.basePrice());`
);

code = code.replace(
  'model.setImageUrl(request.imageUrl());\n            PokemonModel saved = pokemonModelRepository.save(model);',
  `model.setImageUrl(request.imageUrl());
            model.setBasePrice(request.basePrice());
            PokemonModel saved = pokemonModelRepository.save(model);`
);

code = code.replace(
  'model.getImageUrl(),\n                model.getIsActive(),',
  `model.getImageUrl(),
                model.getBasePrice(),
                model.getIsActive(),`
);

fs.writeFileSync('src/main/java/com/pokeprint/api/controller/PokemonModelController.java', code);
