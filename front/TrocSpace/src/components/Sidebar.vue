<template>
  <div class="sidebar-container">
    <div class="sidebar">
      <h4>Menu</h4>
      <ul class="sidebar-list">
        <!-- Onglets classiques -->
        <li v-for="item in menuItems" :key="item.name" :class="{ active: item.active }">
          <a :href="item.href" class="sidebar-link">
            <i :class="item.icon" class="sidebar-icon"></i>
            <span>{{ item.label }}</span>
          </a>
        </li>

        <!-- Texte indiquant que les onglets suivants sont pour les admins -->
        <li v-if="isAdmin" class="admin-info">
          <p class="admin-info-text">Onglets réservés aux admins</p>
        </li>

        <!-- Onglets admin, visibles uniquement si l'utilisateur est admin -->
        <li v-if="isAdmin" v-for="item in adminMenuItems" :key="item.name" :class="{ active: item.active }">
          <a :href="item.href" class="sidebar-link">
            <i :class="item.icon" class="sidebar-icon"></i>
            <span>{{ item.label }}</span>
          </a>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "Sidebar",
  data() {
    return {
      authToken: localStorage.getItem("auth_token"), // Récupération du token d'authentification
      menuItems: [
        { name: "statistics", href: "#", icon: "bi bi-bar-chart", label: "Statistiques", active: false },
        { name: "discussions", href: "#", icon: "bi bi-chat-dots", label: "Discussions", active: false },
        { name: "ads", href: "#", icon: "bi bi-card-list", label: "Mes Annonces", active: false },
        { name: "exchanges", href: "#", icon: "bi bi-arrow-left-right", label: "Mes Échanges", active: false },
        { name: "notifications", href: "#", icon: "bi bi-bell", label: "Notifications", active: false },
        { name: "profile", href: "/profile", icon: "bi bi-person", label: "Mon Profil", active: false },
        { name: "settings", href: "/settings", icon: "bi bi-gear", label: "Paramètres", active: false },
      ],
      adminMenuItems: [
        { name: "user-management", href: "/admin/users", icon: "bi bi-person-lines-fill", label: "Gestion des Utilisateurs", active: false },
        { name: "certification-management", href: "/admin/users-with-selfie", icon: "bi bi-shield-lock", label: "Vérifications", active: false },
        { name: "verified-users-management", href: "/admin/verified-users", icon: "bi bi-check-circle", label: "Utilisateurs Vérifiés", active: false },
      ],
      userRole: null, // Rôle de l'utilisateur (par défaut null)
      errorMessage: null, // Message d'erreur à afficher
      isLoading: false, // Indique si la requête est en cours
    };
  },
  computed: {
    // Détermine si l'utilisateur est admin
    isAdmin() {
      return this.userRole === "ADMIN";
    },
  },
  mounted() {
    this.fetchUserRole(); // Charge le rôle de l'utilisateur au montage
  },
  methods: {
    async fetchUserRole() {
      this.isLoading = true;
      try {
        const url = "/user/me"; // URL de l'API
        console.log(`Requête API vers : ${url}`); // Log pour debug
        const response = await axios.get(url, {
          headers: { Authorization: `Bearer ${this.authToken}` },
        });
        console.log("Réponse API : ", response.data); // Vérifiez la structure des données
        this.userRole = response.data.role || "UNKNOWN"; // Définit le rôle de l'utilisateur
      } catch (error) {
        this.errorMessage = "Erreur lors de la récupération des informations utilisateur.";
        console.error("Erreur API : ", error); // Log pour debug
      } finally {
        this.isLoading = false;
      }
    },
  },
};
</script>

<style scoped>
.sidebar-container {
  padding: 1rem;
}

.sidebar {
  padding: 1rem;
  border: 1px solid black;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.sidebar h4 {
  font-size: 1.2rem;
  margin-bottom: 1rem;
}

.sidebar-list {
  list-style: none;
  padding: 0;
}

.sidebar-list li {
  margin: 0.5rem 0;
}

.sidebar-link {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: #333;
  padding: 0.5rem;
  border-radius: 5px;
  transition: background-color 0.2s;
}

.sidebar-link:hover {
  background-color: #e9ecef;
}

.sidebar-link .sidebar-icon {
  margin-right: 0.5rem;
  font-size: 1.2rem;
}

.sidebar-list .active .sidebar-link {
  background-color: #007bff;
  color: #fff;
}

/* Styles pour le texte informatif */
.admin-info {
  margin-top: 1rem;
  margin-bottom: 1rem;
}

.admin-info-text {
  font-size: 0.9rem;
  color: #007bff;
  font-weight: bold;
}
</style>
