import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import 'bootstrap/dist/css/bootstrap.min.css'; // Import CSS de Bootstrap
import 'bootstrap-icons/font/bootstrap-icons.css'; // Import des icônes de Bootstrap
import './assets/styles/global.css'; // Si vous avez des styles personnalisés

// Importer Bootstrap JS avec Popper.js (inclus dans le bundle)
import 'bootstrap/dist/js/bootstrap.bundle.min.js'; // Ceci est nécessaire pour les dropdowns, modals, etc.

// Importer Axios
import axios from 'axios';

// Configurer l'instance Axios pour appeler l'API Spring Boot
axios.defaults.baseURL = 'http://localhost:8080/api'; // L'URL de votre API Spring Boot
axios.defaults.headers.common['Content-Type'] = 'application/json'; // Définir le type de contenu
axios.defaults.withCredentials = true; // Si vous utilisez des cookies ou de l'authentification par session

// Ajouter Axios à Vue
const app = createApp(App);
app.config.globalProperties.$axios = axios; // Maintenant, tu peux utiliser axios dans tous tes composants

app.use(router).mount('#app');
