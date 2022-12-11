const { defineConfig } = require('@vue/cli-service')
const path = require("path");

const defineCfg = defineConfig({
  transpileDependencies: true,
  outputDir: path.resolve(__dirname, "../resources/static"),
});
module.exports = {
  ...defineCfg,
  css: {
    loaderOptions: {
      sass: {
        prependData: '@import "@/assets/scss/style.scss";'
      }
    }
  }
}
