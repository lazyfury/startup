import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import babel from '@rollup/plugin-babel'
import legacy from '@vitejs/plugin-legacy'
import UnoCSS from 'unocss/vite'



// https://vite.dev/config/
export default defineConfig({
  plugins: [vue(), legacy({
    targets: ["ie >= 11", "chrome >= 100"],
  }), UnoCSS()],
  server: {
    host: "127.0.0.1",
    cors: true,
    fs: {
      strict: false,
      allow: [
        "../..",
        "../../up_run/src/main/resources/templates"
      ]
    }
  },

  // alias 
  resolve: {
    alias: {
    },
  },

  build: {
    manifest: "manifest.json",
    outDir: "../public",
    emptyOutDir: true,
    sourcemap: true,
    minify: true,
    assetsDir: "assets",
    modulePreload: {
      polyfill: true,
    },
    assetsInlineLimit: 0,
    rollupOptions: {
      input: [
        "src/main.ts",
        "src/style.scss",
      ],
      plugins: [
        // 其他插件...
        babel({
          babelHelpers: "bundled",
          presets: [
            [
              "@babel/preset-env",
              {
                corejs: 3,
                useBuiltIns: "entry",
                targets: {
                  chrome: "100",
                  edge: "100",
                  firefox: "100",
                  safari: "100",
                  ie: "11",
                },
              },
            ],
          ],
          exclude: "node_modules/**",
        }),
      ],
    },
  }
})
