import { defineConfig,presetAttributify,presetWind4,presetIcons } from 'unocss'

export default defineConfig({
    presets: [
        presetAttributify(),
        presetWind4(),
        presetIcons(),
    ],
    content: {
        filesystem: [
            '../../up_app/src/main/resources/templates/**/*.html',
        ]
    },
    theme: {
        colors: {
            primary: '#4233cf',
            secondary: '#0F609B',
            accent: '#F59E0B',
            background: '#F9F9F9',
            "base-text": "#333",
            "reverse-primary": "#ff8c00",
        }
    },
    shortcuts: {
        'content-width': 'md:w-80% xl:w-1000px 2xl:w-1200px mx-auto',
        "btn": "px-4 py-2 rounded-md font-bold",
        "btn-outline": "btn border border-primary text-primary",
        "btn-primary": "btn bg-primary text-white",
        "btn-disabled": "btn bg-gray-300 text-gray-500 cursor-not-allowed",
        "card": "bg-white p-4 rounded-md border border-gray-200",
        "card-title": "text-2xl font-bold text-primary",
        "card-text": "text-base text-gray-600",
        "card-body": "p-4",

        "link": "text-primary underline",
    }
})