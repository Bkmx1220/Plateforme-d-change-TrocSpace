<template>
  <LayoutSidebar>
    <div class="container-fluid mt-4">
      <div class="card shadow-lg" style="max-width: 80em; width: 100%; margin: auto;">
        <div class="card-body">
          <h4 class="card-title">Liste des utilisateurs</h4>

          <!-- Afficher un spinner si en chargement -->
          <div v-if="isLoading" class="spinner-border text-primary" role="status">
            <span class="sr-only">Chargement...</span>
          </div>

          <!-- Afficher un message d'erreur si nécessaire -->
          <div v-else-if="errorMessage" class="alert alert-danger">
            {{ errorMessage }}
          </div>

          <!-- Vérification des droits d'accès -->
          <div v-else-if="!isAdmin" class="alert alert-warning">
            Vous n'avez pas les droits nécessaires pour accéder à cette page.
          </div>

          <!-- Affichage des utilisateurs si tout est OK -->
          <div v-else>
            <table class="table table-striped table-hover table-bordered">
              <thead>
              <tr>
                <th scope="col">Photo</th>
                <th scope="col">Nom</th>
                <th scope="col">Email</th>
                <th scope="col">Statut</th>
                <th scope="col">Action</th>
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
                  <button class="btn btn-primary btn-sm" @click="verifyUser(user)">
                    Vérifier
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
import defaultAvatar from "@/assets/images/default_avatar.jpg";

export default {
  components: { LayoutSidebar },
  data() {
    return {
      users: [],
      isLoading: true,
      errorMessage: "",
      authToken: localStorage.getItem("auth_token"),
      isAdmin: false,
      defaultAvatar: defaultAvatar,
    };
  },
  mounted() {
    if (!this.authToken) {
      window.location.href = "/login";
    } else {
      this.fetchUserRole();
    }
  },
  methods: {
    async fetchUserRole() {
      try {
        const response = await axios.get("http://localhost:8080/api/user/me", {
          headers: { Authorization: `Bearer ${this.authToken}` },
        });
        this.isAdmin = response.data.role === "ADMIN";

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
      // Appel de la route qui récupère les utilisateurs avec un selfie
      try {
        const response = await axios.get("http://localhost:8080/api/admin/users/with-selfie-unverified", {
          headers: { Authorization: `Bearer ${this.authToken}` },
        });
        this.users = response.data; // Mise à jour de la liste des utilisateurs
      } catch (error) {
        this.errorMessage = "Impossible de récupérer la liste des utilisateurs avec un selfie.";
      }
    },

    async verifyUser(user) {
      // Vérifie si l'utilisateur est valide
      if (!user || !user.id || !user.email) {
        alert("Utilisateur invalide.");
        return;
      }

      // Affiche une boîte de confirmation avant de vérifier l'utilisateur
      const confirmation = confirm(`Êtes-vous sûr de vouloir vérifier l'utilisateur ${user.email} ?`);

      if (confirmation) {
        try {
          // Appel à la route de vérification
          const response = await axios.put(`http://localhost:8080/api/admin/users/${user.id}/verify`, null, {
            headers: { Authorization: `Bearer ${this.authToken}` },
          });

          alert(`L'utilisateur avec l'adresse e-mail ${user.email} a été vérifié !`);
          await this.fetchUsers(); // Recharger les utilisateurs après vérification
        } catch (error) {
          alert("Erreur lors de la vérification de l'utilisateur.");
        }
      } else {
        alert("La vérification a été annulée.");
      }
    }
  }
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

.table td img {
  border-radius: 50%;
  width: 50px;
  height: 50px;
  object-fit: cover;
  border: 2px solid #ddd;
}

.btn-sm {
  padding: 0.25rem 0.5rem;
  font-size: 0.875rem;
}

.table td button {
  transition: all 0.3s ease;
}

.table td button:hover {
  transform: scale(1.05);
}
</style>
