<template>
  <LayoutSidebar>
    <div class="container-fluid mt-4">
      <div class="card shadow-lg" style="max-width: 80em; width: 100%; margin: auto;">
        <div class="card-body">
          <h4 class="card-title">Utilisateurs Vérifiés</h4>

          <!-- Afficher un spinner si en chargement -->
          <div v-if="isLoading" class="spinner-container">
            <div class="spinner-border text-primary" role="status">
              <span class="sr-only">Chargement...</span>
            </div>
          </div>

          <!-- Afficher un message d'erreur si nécessaire -->
          <div v-else-if="errorMessage" class="alert alert-danger">
            {{ errorMessage }}
          </div>

          <!-- Affichage des utilisateurs vérifiés -->
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
              <tr v-for="user in verifiedUsers" :key="user.id">
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
                  <button class="btn btn-danger btn-sm" @click="removeVerification(user.id)">
                    Enlever la vérification
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
      verifiedUsers: [], // Liste des utilisateurs vérifiés
      isLoading: true,
      errorMessage: "",
      authToken: localStorage.getItem("auth_token"),
      defaultAvatar: defaultAvatar,
    };
  },
  mounted() {
    if (!this.authToken) {
      window.location.href = "/login"; // Rediriger si le token est absent
    } else {
      this.loadVerifiedUsers();
    }
  },
  methods: {
    // Charger les utilisateurs vérifiés
    async loadVerifiedUsers() {
      try {
        const response = await axios.get("http://localhost:8080/api/admin/users/verified", {
          headers: { Authorization: `Bearer ${this.authToken}` },
        });
        this.verifiedUsers = response.data; // Mise à jour de la liste des utilisateurs vérifiés
      } catch (error) {
        this.errorMessage = "Impossible de récupérer les utilisateurs vérifiés.";
      } finally {
        this.isLoading = false;
      }
    },

    // Enlever la vérification d'un utilisateur
    async removeVerification(userId) {
      // Demande de confirmation avant de retirer la vérification
      const confirmation = confirm("Êtes-vous sûr de vouloir désactiver la vérification de cet utilisateur ?");

      if (confirmation) {
        try {
          const response = await axios.put(`/admin/users/${userId}/unverify`, {}, {
            headers: { Authorization: `Bearer ${this.authToken}` },
          });

          // Affichage de l'alerte et rechargement de la liste
          alert("L'utilisateur a été désvérifié !");

          // Recharger la liste des utilisateurs vérifiés
          await this.loadVerifiedUsers();
        } catch (error) {
          alert("Erreur lors de la suppression de la vérification.");
        }
      } else {
        alert("La désactivation de la vérification a été annulée.");
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
  padding: 12px;
}

.table th {
  background-color: #f8f9fa;
  color: #495057;
  font-weight: bold;
}

.table td {
  background-color: #ffffff;
}

.table td img {
  border-radius: 50%;
  width: 50px;
  height: 50px;
  object-fit: cover;
  border: 2px solid #ddd;
}

.table td button {
  padding: 0.25rem 0.5rem;
  font-size: 0.875rem;
  transition: all 0.3s ease;
}

.table td button:hover {
  transform: scale(1.05);
}

.text-success {
  color: #28a745 !important;
}

.text-danger {
  color: #dc3545 !important;
}

.btn-primary, .btn-danger {
  border-radius: 5px;
  font-weight: 600;
  text-transform: uppercase;
}

.card-body {
  background-color: #f8f9fa;
}

.spinner-container {
  position: relative;
  height: 200px; /* Définir une hauteur fixe pour le spinner */
  display: flex;
  justify-content: center;
  align-items: center;
}

.spinner-border {
  width: 3rem;
  height: 3rem;
}

.alert {
  font-weight: 600;
}

.table-striped tbody tr:nth-child(odd) {
  background-color: #f2f2f2;
}

.table-striped tbody tr:nth-child(even) {
  background-color: #ffffff;
}

.table-bordered td, .table-bordered th {
  border: 1px solid #ddd;
}

.table-hover tbody tr:hover {
  background-color: #f1f1f1;
}

@media (max-width: 767px) {
  .table td img {
    width: 40px;
    height: 40px;
  }

  .table th, .table td {
    padding: 8px;
  }

  .btn-sm {
    font-size: 0.75rem;
    padding: 0.2rem 0.4rem;
  }
}
</style>
