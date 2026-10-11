const fs = require('fs');

// Request DTO
let req = fs.readFileSync('src/main/java/com/pokeprint/api/dto/request/PokemonModelRequestDTO.java', 'utf8');
req = req.replace(
  'String imageUrl\n) {}',
  `String imageUrl,
    @PositiveOrZero BigDecimal basePrice
) {}`
);
fs.writeFileSync('src/main/java/com/pokeprint/api/dto/request/PokemonModelRequestDTO.java', req);

// Response DTO
let res = fs.readFileSync('src/main/java/com/pokeprint/api/dto/response/PokemonModelResponseDTO.java', 'utf8');
res = res.replace(
  'String imageUrl,',
  `String imageUrl,
    BigDecimal basePrice,`
);
fs.writeFileSync('src/main/java/com/pokeprint/api/dto/response/PokemonModelResponseDTO.java', res);

// Mapper
let map = fs.readFileSync('src/main/java/com/pokeprint/api/mapper/PokemonModelMapper.java', 'utf8');
map = map.replace(
  'dto.imageUrl(),',
  `dto.imageUrl(),
            model.getBasePrice(),`
);
map = map.replace(
  'model.setImageUrl(dto.imageUrl());',
  `model.setImageUrl(dto.imageUrl());
        model.setBasePrice(dto.basePrice());`
);
fs.writeFileSync('src/main/java/com/pokeprint/api/mapper/PokemonModelMapper.java', map);
