const fs = require('fs');
const path = require('path');

function stripBOM(dir) {
    const files = fs.readdirSync(dir);
    for (const file of files) {
        const fullPath = path.join(dir, file);
        if (fs.statSync(fullPath).isDirectory()) {
            stripBOM(fullPath);
        } else if (fullPath.endsWith('.java')) {
            const buf = fs.readFileSync(fullPath);
            if (buf[0] === 0xEF && buf[1] === 0xBB && buf[2] === 0xBF) {
                console.log('Stripping BOM from ' + fullPath);
                fs.writeFileSync(fullPath, buf.slice(3));
            }
        }
    }
}

stripBOM('src/main/java');