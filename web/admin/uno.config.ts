import { defineConfig } from 'unocss'
import presetWind4 from '@unocss/preset-wind4'

export default defineConfig({
  presets: [
    presetWind4()
  ],
  theme: {
    colors: {
      primary: '#1890FF'
    }
  }
})
