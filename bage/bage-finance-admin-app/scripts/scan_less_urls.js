/**
 * 扫描 LESS 文件中的 url(...) 引用，验证本地路径能否解析。
 * 用法：node scripts/scan_less_urls.js  （或用 ts-node/tsx 运行 ts 版本）
 */
const fs = require('fs');
const path = require('path');

const root = path.resolve(__dirname, '..');
const lessDirs = [path.join(root, 'src')];

// 提取 url(...) 的正则：支持带引号 / 不带引号
const urlRe = /url\(\s*(['"]?)([^'")]+?)\1\s*\)/g;

function* walk(dir) {
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const full = path.join(dir, entry.name);
    if (entry.isDirectory()) {
      yield* walk(full);
    } else if (entry.name.endsWith('.less')) {
      yield full;
    }
  }
}

const problems = [];

for (const dir of lessDirs) {
  if (!fs.existsSync(dir)) continue;
  for (const file of walk(dir)) {
    const content = fs.readFileSync(file, 'utf8');
    let m;
    while ((m = urlRe.exec(content)) !== null) {
      const quote = m[1];       // '' | "'" | '"'
      const raw = m[2].trim();  // url 内部原始字符串

      // 远程地址（http/https/data/cdn）直接跳过
      if (/^(https?:)?\/\//i.test(raw) || /^data:/i.test(raw)) continue;

      // 定位行号
      const idx = m.index;
      const line = content.slice(0, idx).split('\n').length;

      // 去掉查询串/锚点
      const clean = raw.split(/[?#]/)[0];
      const resolved = path.resolve(path.dirname(file), clean);
      const exists = fs.existsSync(resolved);

      problems.push({
        file,
        line,
        url: raw,
        quote,
        resolved,
        exists,
      });
    }
  }
}

if (problems.length === 0) {
  console.log('✅ 未发现无法解析的本地 url(...) 引用。');
} else {
  console.log('发现以下 url(...) 引用需要关注：\n');
  for (const p of problems) {
    const status = p.exists ? '✅ 可解析' : '❌ 无法解析';
    console.log(`${status}  ${path.relative(root, p.file)}:${p.line}`);
    console.log(`    url: ${p.url}`);
    console.log(`    解析到: ${p.resolved}`);
    if (!p.quote) console.log('    ⚠️  未加引号，建议加上单引号');
    console.log('');
  }
  process.exitCode = 1;
}
