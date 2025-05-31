<template>
  <LayoutSidebar>
    <div class="profile-container">
      <div class="card shadow-lg profile-card">
        <div class="card-body">
          <h3 class="text-center mb-4">Modifier le profil</h3>

          <!-- Message contextuel -->
          <div v-if="successMessage" class="alert alert-success">
            {{ successMessage }}
          </div>

          <!-- Formulaire de modification -->
          <form @submit.prevent="updateProfile">
            <!-- Photo de Profil -->
            <div class="mb-4 text-center">
              <img
                  :src="photoProfil || defaultAvatar"
                  alt="Photo de profil"
                  class="rounded-circle border border-4 border-light shadow"
                  width="150"
                  height="150"
              />
              <div class="mt-3">
                <input
                    type="file"
                    id="photoProfil"
                    @change="handleFileChange"
                    class="form-control mb-2"
                />

                <!-- Bouton de suppression conditionnel -->
                <button
                    v-if="photoProfil !== defaultAvatar && !isTempPhoto"
                    type="button"
                    @click="deletePhoto"
                    class="btn btn-danger btn-sm"
                >
                  Supprimer la photo
                </button>
              </div>
            </div>

            <!-- Nom et Prénom -->
            <div class="mb-3">
              <label for="prenom" class="form-label">Prénom</label>
              <input
                  type="text"
                  id="prenom"
                  v-model="user.prenom"
                  class="form-control"
                  required
              />
              <span v-if="errors.prenom" class="text-danger">{{ errors.prenom }}</span>
            </div>

            <div class="mb-3">
              <label for="nom" class="form-label">Nom</label>
              <input
                  type="text"
                  id="nom"
                  v-model="user.nom"
                  class="form-control"
                  required
              />
              <span v-if="errors.nom" class="text-danger">{{ errors.nom }}</span>
            </div>

            <!-- Boutons de sauvegarde et d'annulation -->
            <div class="d-flex justify-content-between mt-3">
              <button
                  type="button"
                  @click="cancelChanges"
                  class="btn btn-secondary w-auto"
              >
                Annuler
              </button>
              <button
                  type="submit"
                  class="btn btn-success w-auto ms-3"
              >
                Sauvegarder les modifications
              </button>
            </div>

          </form>
        </div>
      </div>
    </div>
  </LayoutSidebar>
</template>

<script>
import axios from "axios";
import defaultAvatar from "@/assets/images/default_avatar.jpg";
import LayoutSidebar from "@/layouts/LayoutSidebar.vue";

export default {
  components: { LayoutSidebar },
  data() {
    return {
      user: { prenom: "", nom: "", email: "" },
      photoProfil: "",
      defaultAvatar: defaultAvatar,
      selectedFile: null,
      isTempPhoto: false, // Nouveau état pour gérer les photos temporaires
      authToken: localStorage.getItem("auth_token"),
      errors: {},
      successMessage: "",
    };
  },
  mounted() {
    this.fetchUserData();
  },
  methods: {
    async fetchUserData() {
      try {
        const response = await axios.get("/verification/details", {
          headers: { Authorization: `Bearer ${this.authToken}` },
        });
        if (response.status === 200) {
          this.user = response.data;
          this.photoProfil = response.data.photoProfil
              ? response.data.photoProfil
              : this.defaultAvatar;
          this.isTempPhoto = false; // Photo actuelle est stockée sur le serveur
        }
      } catch (error) {
        console.error("Erreur lors de la récupération des informations", error);
      }
    },

    handleFileChange(event) {
      const file = event.target.files[0];
      if (!["image/jpeg", "image/png"].includes(file.type) || file.size > 2 * 1024 * 1024) {
        alert("Sélectionnez une image JPEG ou PNG (max 2 Mo).");
        return;
      }
      if (this.photoProfil && this.selectedFile) {
        URL.revokeObjectURL(this.photoProfil); // Libérer l'ancienne URL
      }
      this.selectedFile = file;
      this.photoProfil = URL.createObjectURL(file);
      this.isTempPhoto = true; // Indique que la photo est temporaire
    },

    async deletePhoto() {
      try {
        const response = await axios.delete("/verification/delete/photoProfil", {
          headers: { Authorization: `Bearer ${this.authToken}` },
        });
        if (response.status === 200) {
          this.photoProfil = this.defaultAvatar;
          this.isTempPhoto = false; // La photo n'est plus temporaire
          this.successMessage = "Photo de profil supprimée avec succès.";
        }
      } catch (error) {
        console.error("Erreur lors de la suppression de la photo de profil", error);
      }
    },

    async updateProfile() {
      this.errors = {};
      const userData = { nom: this.user.nom, prenom: this.user.prenom };

      try {
        // Mise à jour des données utilisateur
        const response = await axios.put("/user/update", userData, {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${this.authToken}`,
          },
        });

        // Upload de la photo de profil si un fichier est sélectionné
        if (this.selectedFile) {
          const formData = new FormData();
          formData.append("photoProfil", this.selectedFile);

          await axios.post("/verification/upload", formData, {
            headers: {
              "Content-Type": "multipart/form-data",
              Authorization: `Bearer ${this.authToken}`,
            },
          });
        }

        if (response.status === 200) {
          this.successMessage = "Profil mis à jour avec succès.";
          this.$router.push({ name: 'Profile' });
        }
      } catch (error) {
        if (error.response && error.response.status === 422) {
          this.errors = error.response.data.errors;
        } else {
          console.error("Erreur lors de la mise à jour du profil", error);
        }
      }
    },

    cancelChanges() {
      // Rediriger l'utilisateur vers la page de profil sans sauvegarder les modifications
      this.$router.push({ name: 'Profile' });
    },
  },
};
</script>


<style scoped>
.profile-container {
  display: flex;
  justify-content: flex-start;
  align-items: flex-start;
  margin-top: 1em;
}

.profile-card {
  max-width: 100%;
  flex: 1;
  background-color: #f8f9fa;
  border-radius: 1rem;
  padding: 2rem;
  margin-left: 0;
}

img {
  object-fit: cover;
  border: 4px solid #f8f9fa;
}

button {
  width: 150px;
}
</style>

