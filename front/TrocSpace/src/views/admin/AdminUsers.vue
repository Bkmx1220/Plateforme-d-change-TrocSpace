<template>
  <LayoutSidebar>
    <div class="container-fluid mt-4">
      <div class="card shadow-lg" style="max-width: 80em; width: 100%; margin: auto;">
        <div class="card-body">
          <h4 class="card-title">Liste des utilisateurs</h4>

          <!-- Vérifier si l'utilisateur est admin -->
          <div v-if="isLoading" class="spinner-border text-primary" role="status">
            <span class="sr-only">Chargement...</span>
          </div>

          <div v-else-if="errorMessage" class="alert alert-danger">
            {{ errorMessage }}
          </div>

          <div v-else-if="!isAdmin" class="alert alert-warning">
            Vous n'avez pas les droits nécessaires pour accéder à cette page.
          </div>

          <div v-else>
            <table class="table table-striped table-hover table-bordered">
              <thead>
              <tr>
                <th scope="col">Photo</th>
                <th scope="col">Nom</th>
                <th scope="col">Email</th>
                <th scope="col">Statut</th>
                <th scope="col">Actions</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="user in users" :key="user.id">
                <td>
                  <a :href="`/user/${user.id}`">
                    <img
                        :src="user.verification && user.verification.photoProfil
                         ? `http://localhost:8080/api/verification/image/${user.verification.photoProfil}`
                         : defaultAvatar"
                        alt="Photo de profil"
                        class="img-fluid rounded-circle"
                    />

                  </a>
                </td>
                <td>{{ user.nom }} {{ user.prenom }}</td>
                <td>{{ user.email }}</td>
                <td>
                  <span :class="user.enabled ? 'text-success' : 'text-danger'">
                    {{ user.enabled ? 'Activé' : 'Désactivé' }}
                  </span>
                </td>
                <td>
                  <button
                      @click="toggleUserStatus(user.id)"
                      :class="['btn', user.enabled ? 'btn-warning' : 'btn-success', 'btn-sm']">
                    {{ user.enabled ? "Désactiver" : "Activer" }}
                  </button>
                  <button
                      @click="deleteUser(user.id)"
                      class="btn btn-danger btn-sm ms-2"
                      :disabled="!isAdmin">
                    Supprimer
                  </button>
                </td>
              </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </LayoutSidebar>
</template>

<script>
import axios from "axios";
import LayoutSidebar from "@/layouts/LayoutSidebar.vue";
import defaultAvatar from "@/assets/images/default_avatar.jpg";  // Importer l'image par défaut

export default {
  components: { LayoutSidebar },
  data() {
    return {
      users: [],
      isLoading: true,
      errorMessage: "",
      authToken: localStorage.getItem("auth_token"),
      userRole: null,  // Stocke le rôle de l'utilisateur
      isAdmin: false,  // Détermine si l'utilisateur est admin
      defaultAvatar: defaultAvatar,  // Définir l'image par défaut
    };
  },
  mounted() {
    if (!this.authToken) {
      window.location.href = "/login";
    } else {
      this.fetchUserRole(); // Récupérer le rôle au montage du composant
    }
  },
  methods: {
    async fetchUserRole() {
      this.isLoading = true;
      try {
        const response = await axios.get("http://localhost:8080/api/user/me", {
          headers: { Authorization: `Bearer ${this.authToken}` },
        });
        this.userRole = response.data.role;
        this.isAdmin = this.userRole === "ADMIN";

        if (this.isAdmin) {
          await this.fetchUsers();
        }
      } catch (error) {
        this.errorMessage = "Erreur lors de la récupération des informations utilisateur.";
      } finally {
        this.isLoading = false;
      }
    },

    async fetchUsers() {
      try {
        const response = await axios.get("http://localhost:8080/api/admin/users", {
          headers: { Authorization: `Bearer ${this.authToken}` },
        });
        this.users = response.data;
      } catch (error) {
        this.errorMessage = "Impossible de récupérer la liste des utilisateurs.";
      }
    },

    async toggleUserStatus(userId) {
      // Confirmation avant de modifier le statut de l'utilisateur
      if (!confirm("Êtes-vous sûr de vouloir changer l'état de cet utilisateur ?")) {
        return; // Si l'utilisateur annule, ne rien faire
      }

      const url = `/admin/users/${userId}/toggle-status`;
      try {
        const response = await axios.put(url, null, {
          headers: { Authorization: `Bearer ${this.authToken}` },
        });
        alert(response.data);
        await this.fetchUsers(); // Recharger les utilisateurs
      } catch (error) {
        alert("Erreur lors de la modification de l'état de l'utilisateur.");
      }
    },

    async deleteUser(userId) {
      // Confirmation avant de supprimer l'utilisateur
      if (!confirm("Êtes-vous sûr de vouloir supprimer cet utilisateur ?")) {
        return; // Si l'utilisateur annule, ne rien faire
      }

      const url = `/admin/users/${userId}`;
      try {
        const response = await axios.delete(url, {
          headers: { Authorization: `Bearer ${this.authToken}` },
        });
        alert(response.data);
        await this.fetchUsers(); // Recharger les utilisateurs
      } catch (error) {
        alert("Erreur lors de la suppression de l'utilisateur.");
      }
    },
  },
};
</script>

<style scoped>
.container-fluid {
  max-width: 100%;
  padding: 0;
}

.card {
  border-radius: 15px;
}

.table th, .table td {
  vertical-align: middle;
  border: 1px solid #ddd;
}

.table th {
  text-align: center;
  background-color: #f8f9fa;
}

.table td {
  text-align: center;
}

.table-responsive {
  overflow-x: auto;
  display: block;
}

.table-striped tbody tr:nth-of-type(odd) {
  background-color: #f9f9f9;
}

.table-hover tbody tr:hover {
  background-color: #f1f1f1;
}

.table td img {
  border-radius: 50%;
  width: 50px;
  height: 50px;
  object-fit: cover;
  border: 2px solid #ddd;
}

.table td button {
  transition: all 0.3s ease;
}

.table td button:hover {
  transform: scale(1.05);
}

.btn-sm {
  padding: 0.25rem 0.5rem;
  font-size: 0.875rem;
}

</style>
