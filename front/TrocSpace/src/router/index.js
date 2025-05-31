import { createRouter, createWebHistory } from 'vue-router';
import Login from '../views/auth/Login.vue';
import Register from '../views/auth/Register.vue';
import Home from '../views/Home.vue';
import Annonces from '../views/Annonces.vue';
import APropos from '../views/APropos.vue';
import Contact from '../views/Contact.vue';
import Profile from "@/views/user/Profile.vue";
import EditProfile from "@/views/user/EditProfile.vue";
import TestPage from "@/views/TestPage.vue";
import EmailSent from "@/views/EmailSent.vue";
import EmailVerification from "@/views/EmailVerification.vue";
import ResetPassword from "@/views/password_reset/ResetPassword.vue";
import AdminUsers from "@/views/admin/AdminUsers.vue";
import AdminUsersWithSelfie from "@/views/admin/AdminUsersWithSelfie.vue";
import UserProfile from "@/views/user/UserProfile.vue";
import Settings from "@/views/user/Settings.vue";
import verificationPage from "@/views/verificationPage.vue";
import AdminVerifiedUsers from "@/views/admin/AdminVerifiedUsers.vue";

// Définition de la fonction isAuthenticated pour vérifier si un token est valide
function isAuthenticated() {
    const token = localStorage.getItem('auth_token');
    if (!token) return false;

    // Si c'est un JWT, vérifier sa validité
    const decodedToken = JSON.parse(atob(token.split('.')[1])); // Décodage du payload
    const expDate = decodedToken.exp * 1000; // Expiration en millisecondes
    const currentDate = new Date().getTime();

    return currentDate < expDate; // Retourne true si le token n'est pas expiré
}

const routes = [
    {
        path: '/',
        name: 'Home',
        component: Home
    },
    {
        path: '/login',
        name: 'Login',
        component: Login
    },
    {
        path: '/register',
        name: 'Register',
        component: Register
    },
    {
        path: '/annonces',
        name: 'Annonces',
        component: Annonces
    },
    {
        path: '/a-propos',
        name: 'APropos',
        component: APropos
    },
    {
        path: '/contact',
        name: 'Contact',
        component: Contact
    },
    {
        path: '/profile',
        name: 'Profile',
        component: Profile,
        meta: { requiresAuth: true },
    },
    {
        path: '/edit-profile',
        name: 'EditProfile',
        component: EditProfile,
        meta: { requiresAuth: true },
    },
    {
        path: '/test-page',
        name: 'Testpage',
        component: TestPage,
        meta: { requiresAuth: true },
    },
    {
        path: '/email-sent',
        name: 'EmailSent',
        component: EmailSent
    },
    {
        path: '/verify-email',
        name: 'EmailVerification',
        component: EmailVerification
    },
    {
        path: "/reset-password",
        name: "ResetPassword",
        component: ResetPassword,
    },
    {
        path: '/admin/users',
        name: 'AdminUsers',
        component: AdminUsers,
        meta: { requiresAuth: true },
    },
    {
        path: '/admin/users-with-selfie',
        name: 'AdminUsersWithSelfie',
        component: AdminUsersWithSelfie,
        meta: { requiresAuth: true },
    },
    {
        path: '/admin/verified-users',
        name: 'AdminVerifiedUsers',
        component: AdminVerifiedUsers,
        meta: { requiresAuth: true },
    },
    {
        path: '/user/:id',
        name: 'userProfile',
        component: UserProfile,
        meta: { requiresAuth: true }
    },
    {
        path: '/settings',
        name: 'Settings',
        component: Settings,
        meta: { requiresAuth: false }
    },
    {
        path: '/verification-page',
        name: 'verificationPage',
        component: verificationPage,
        meta: { requiresAuth: true }
    }

];

const router = createRouter({
    history: createWebHistory(),
    routes
});

router.beforeEach((to, from, next) => {
    if (to.matched.some(record => record.meta.requiresAuth) && !isAuthenticated()) {
        next({ name: 'Login', query: { redirect: to.fullPath } }); // Redirige vers la page de connexion avec la route de destination
    } else {
        next();
    }
});

export default router;
