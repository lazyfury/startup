import 'virtual:uno.css';
import { createApp } from 'vue';
import HqjhGetUrl from './HqjhGetUrl.vue';
console.log("hello world")

const path = location.pathname;

if (path === "/hqjh/getUrl") {
    console.log("getUrl")
    let app = document.getElementById("app") as HTMLElement;
    console.log(app)
    createApp(HqjhGetUrl).mount(app);
}