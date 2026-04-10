const { defineConfig } = require('@vue/cli-service')
const path = require('path')

module.exports = defineConfig({
  transpileDependencies: true,

  // 开发服务器配置
  devServer: {
    port: 8080,
    host: 'localhost',
    open: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8082',
        changeOrigin: true
      }
    }
  },

  // 打包配置
  outputDir: 'dist',
  assetsDir: 'static',
  productionSourceMap: false,

  // CSS配置
  css: {
    extract: true,
    sourceMap: false,
    loaderOptions: {
      sass: {
        additionalData: `@import "@/styles/variables.scss";`
      }
    }
  },

  // Webpack配置
  configureWebpack: {
    resolve: {
      alias: {
        '@': path.resolve(__dirname, 'src')
      }
    },
    performance: {
      hints: false,
      maxEntrypointSize: 512000,
      maxAssetSize: 512000
    }
  },

  // 插件配置
  pluginOptions: {
    'style-resources-loader': {
      preProcessor: 'scss',
      patterns: [
        path.resolve(__dirname, 'src/styles/variables.scss')
      ]
    }
  },

  // 链式配置
  chainWebpack: config => {
    // 移除 prefetch 插件
    config.plugins.delete('prefetch')

    // 压缩代码
    config.optimization.minimize(true)

    // 分割代码
    config.optimization.splitChunks({
      chunks: 'all',
      cacheGroups: {
        libs: {
          name: 'chunk-libs',
          test: /[\\/]node_modules[\\/]/,
          priority: 10,
          chunks: 'initial'
        },
        elementPlus: {
          name: 'chunk-elementPlus',
          test: /[\\/]node_modules[\\/]_?element-plus(.*)/,
          priority: 20
        },
        commons: {
          name: 'chunk-commons',
          test: path.resolve(__dirname, 'src/components'),
          minChunks: 3,
          priority: 5,
          reuseExistingChunk: true
        }
      }
    })
  }
})
