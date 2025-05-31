<template>
  <Navbar />
  <div class="annonces-container">
    <div class="header">
      <h1>Les Annonces</h1>
      <div class="search-bar">
        <input
            type="text"
            class="form-control search-input"
            placeholder="Rechercher"
            v-model="searchQuery"
        />
      </div>
      <button class="btn btn-primary">+ Nouvelle Annonce</button>
    </div>

    <div class="categories">
      <label for="categories">Catégories:</label>
      <select id="categories" class="form-control" v-model="selectedCategory">
        <option value="">Toutes</option>
        <option value="electronique">Électronique</option>
        <option value="maison-et-jardin">Maison et Jardin</option>
        <option value="mode">Mode</option>
        <option value="sport-et-loisirs">Sport et Loisirs</option>
        <option value="enfants">Enfants</option>
        <option value="autres">Autres</option>
      </select>
    </div>


    <div class="annonces-list">
      <div class="annonce-card" v-for="annonce in filteredAnnonces" :key="annonce.id">
        <!-- Utilisation d'import pour charger les images -->
        <img :src="getImage(annonce.image)" :alt="annonce.titre" class="annonce-image" />
        <div class="annonce-details">
          <h3>{{ annonce.titre }}</h3>
          <p>{{ annonce.description }}</p>
          <p class="annonce-prix">{{ annonce.prix }} TC</p>
          <p class="annonce-location">{{ annonce.localisation }}</p>
          <button class="btn btn-secondary">Discuter</button>
        </div>
      </div>
    </div>

    <div class="pagination">
      <button class="page-btn" v-for="page in pages" :key="page" @click="changePage(page)">
        {{ page }}
      </button>
    </div>
  </div>
</template>

<script>
import Navbar from "@/components/Navbar.vue"; // Assure-toi que le chemin d'importation est correct
export default {
  name: "AnnoncesPage",
  components: {
    Navbar,
  },
  data() {
    return {
      searchQuery: "",
      selectedCategory: "",
      annonces: [
        { id: 1, titre: "Télévision", description: "Télé HD 32 pouces", prix: 50000, localisation: "Lomé", image: "bag.jpg", category: "electronique" },
        { id: 2, titre: "Canapé", description: "Canapé 3 places, neuf", prix: 200000, localisation: "Kara", image: "bag.jpg", category: "maison-et-jardin" },
        { id: 3, titre: "Robe élégante", description: "Robe soirée, taille 38", prix: 10000, localisation: "Aného", image: "bag.jpg", category: "mode" },
        { id: 4, titre: "Vélo de course", description: "Vélo en bon état", prix: 75000, localisation: "Atakpamé", image: "bag.jpg", category: "sport-et-loisirs" },
        { id: 5, titre: "Lit pour enfants", description: "Lit superposé en bois", prix: 120000, localisation: "Sokodé", image: "bag.jpg", category: "enfants" },
        { id: 6, titre: "Appareil photo", description: "Appareil photo vintage", prix: 40000, localisation: "Lomé", image: "bag.jpg", category: "autres" },
      ],
      pages: [1, 2, 3],
      currentPage: 1,
    };
  },
  computed: {
    filteredAnnonces() {
      // Filtrage des annonces par catégorie et recherche
      return this.annonces.filter((annonce) => {
        const matchesCategory =
            this.selectedCategory === "" || annonce.category === this.selectedCategory;
        const matchesSearch =
            this.searchQuery === "" ||
            annonce.titre.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
            annonce.description.toLowerCase().includes(this.searchQuery.toLowerCase());
        return matchesCategory && matchesSearch;
      });
    },
  },
  methods: {
    getImage(imageName) {
      return new URL(`../assets/images/${imageName}`, import.meta.url).href;
    },
    changePage(page) {
      this.currentPage = page;
      console.log("Page changée à", page);
    },
  },
};
</script>


<style scoped>
.annonces-container {
  padding: 20px;
  max-width: 1200px;
  margin: auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-bar {
  flex: 1;
  margin: 0 20px;
}

.search-input {
  width: 100%;
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 25px;
  font-size: 14px;
  outline: none;
}

.search-input::placeholder {
  color: #888;
  font-style: italic;
}

.search-input:focus {
  border-color: #ff5722;
  box-shadow: 0 0 5px rgba(255, 87, 34, 0.3);
}

.categories {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.form-control {
  padding: 8px;
  font-size: 14px;
  border: 1px solid #ddd;
  border-radius: 4px;
  width: 150px;
}

.annonces-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  justify-content: center; /* Centre les cartes */
}

.annonce-card {
  width: calc(50% - 20em); /* 5 produits par ligne */
  min-width: 180px;
  height: 30em; /* Ajuste la hauteur des cartes */
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  background: #fff;
  display: flex;
  flex-direction: column;
  text-align: center;
}


.annonce-image {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.annonce-details {
  padding: 15px;
}

.annonce-prix {
  font-weight: bold;
  color: #ff5722;
}

.annonce-location {
  font-size: 14px;
  color: #888;
  margin-bottom: 10px;
}

.btn {
  padding: 10px 15px;
  font-size: 14px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.btn-primary {
  background-color: #ff5722;
  color: white;
}

.btn-secondary {
  background-color: #ff5722;
  color: white;
}

.pagination {
  text-align: center;
  margin-top: 20px;
}

.page-btn {
  background: none;
  border: 1px solid #ddd;
  padding: 8px 12px;
  margin: 0 5px;
  border-radius: 4px;
  cursor: pointer;
}

.page-btn:hover {
  background-color: #f1f1f1;
}
</style>
