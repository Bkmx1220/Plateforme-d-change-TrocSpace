<template>
  <div>
    <!-- Navbar globalement visible sur toutes les pages -->
    <Navbar />
    <!-- Section Bannière -->
    <section class="hero-banner">
      <div class="overlay"></div>
      <div class="content">
        <h1 class="title">Bienvenue sur notre plateforme de troc !</h1>
        <p class="description">
          Échangez facilement vos objets, services ou compétences avec d'autres utilisateurs.
          Donnez une seconde vie à vos biens et faites des rencontres enrichissantes en économisant. Troc malin, troc gagnant !
        </p>
        <!-- Bouton avec Vue Router -->
        <router-link to="/annonces" class="btn-explore">Explorer</router-link>
      </div>
    </section>

    <!-- Annonces Populaires -->
    <section class="annonces-populaires py-5">
      <div class="container">
        <h2 class="text-center mb-4">Annonces Populaires</h2>
        <div class="row">
          <div class="col-md-4" v-for="item in popularAds" :key="item.id">
            <div class="card mb-4 shadow-sm">
              <img :src="item.image" class="card-img-top" alt="Annonce">
              <div class="card-body">
                <h5 class="card-title">{{ item.title }}</h5>
                <p class="card-text">{{ item.description }}</p>
                <router-link :to="'/annonces/' + item.id" class="btn btn-primary">Voir l'annonce</router-link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Annonces Récentes -->
    <section class="annonces-recentes py-5 bg-light">
      <div class="container">
        <h2 class="text-center mb-4">Annonces Récentes</h2>
        <div class="row">
          <div class="col-md-4" v-for="item in recentAds" :key="item.id">
            <div class="card mb-4 shadow-sm">
              <img :src="item.image" class="card-img-top" alt="Annonce">
              <div class="card-body">
                <h5 class="card-title">{{ item.title }}</h5>
                <p class="card-text">{{ item.description }}</p>
                <router-link :to="'/annonces/' + item.id" class="btn btn-primary">Voir l'annonce</router-link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Autres Sections -->
    <section class="autres-sections py-5">
      <div class="container">
        <h2 class="text-center mb-4">Explorez par Catégorie</h2>
        <div class="row">
          <div class="col-md-3" v-for="category in categories" :key="category.id">
            <div class="card mb-4 shadow-sm">
              <img :src="category.image" class="card-img-top" alt="Catégorie">
              <div class="card-body">
                <h5 class="card-title">{{ category.name }}</h5>
                <router-link :to="'/categorie/' + category.id" class="btn btn-outline-primary">Voir les annonces</router-link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
import Navbar from "@/components/Navbar.vue";

export default {
  name: 'Home',
  components: {Navbar},
  data() {
    return {
      popularAds: [
        { id: 1, title: 'Vélo de course', description: 'Vélo neuf, très peu servi', image: '/assets/images/velo.jpg' },
        { id: 2, title: 'Canapé confortable', description: 'Canapé en cuir, presque neuf', image: '/assets/images/canape.jpg' },
        { id: 3, title: 'Smartphone Samsung', description: 'Smartphone presque neuf', image: '/assets/images/phone.jpg' }
      ],
      recentAds: [
        { id: 4, title: 'Télévision 4K', description: 'Télévision Samsung 4K', image: '/assets/images/tv.jpg' },
        { id: 5, title: 'Chaise de bureau', description: 'Chaise ergonomique', image: '/assets/images/chaise.jpg' },
        { id: 6, title: 'Sac à dos', description: 'Sac à dos pour randonnée', image: '/assets/images/sac.jpg' }
      ],
      categories: [
        { id: 1, name: 'Véhicules', image: '/assets/images/vehicule.jpg' },
        { id: 2, name: 'Immobilier', image: '/assets/images/immobilier.jpg' },
        { id: 3, name: 'Électronique', image: '/assets/images/electronique.jpg' },
        { id: 4, name: 'Mode', image: '/assets/images/mode.jpg' }
      ]
    };
  }
};
</script>

<style scoped>
/* Bannière principale */
/* Styles généraux */
.hero-banner {
  position: relative;
  width: 100%;
  height: 100vh; /* Pleine hauteur d'écran */
  background-image: url("@/assets/images/banner_background.jpg"); /* Remplacez par votre image */
  background-size: cover;
  background-position: center;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: left;
  color: #fff;
}

/* Overlay pour obscurcir l'image */
.overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5); /* Opacité ajustable */
  z-index: 1;
}

/* Contenu au-dessus de l'overlay */
.content {
  position: relative;
  z-index: 2;
  max-width: 60em;
  padding: 20px;
}

.title {
  font-size: 3rem;
  font-weight: bold;
  margin-bottom: 20px;
}

.description {
  font-size: 1.2rem;
  line-height: 1.5;
  margin-bottom: 30px;
}

/* Bouton stylisé */
.btn-explore {
  display: inline-block;
  padding: 10px 20px;
  font-size: 1.2rem;
  color: #fff;
  background-color: #ff6600; /* Couleur orange */
  border: none;
  border-radius: 5px;
  text-decoration: none;
  transition: background 0.3s ease;
}

.btn-explore:hover {
  background-color: #e05500;
}

/* Annonces Populaires, Récentes et Catégories */
.annonces-populaires, .annonces-recentes, .autres-sections {
  background-color: #fff;
  padding: 20px 0;
}

.card img {
  height: 200px;
  object-fit: cover;
}

.card-body {
  text-align: center;
}

.card-title {
  font-weight: bold;
}

.card-text {
  font-size: 0.9rem;
  color: #666;
}

.card-body .btn {
  margin-top: 10px;
}

.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.autres-sections .card {
  background-color: #f8f9fa;
}

.autres-sections .card-body {
  text-align: center;
}

/* Responsive */
@media (max-width: 768px) {
  .hero-banner h1 {
    font-size: 2.5rem;
  }
  .hero-banner p {
    font-size: 1rem;
  }
  .hero-banner .btn-primary {
    font-size: 1rem;
  }

  .card img {
    height: 150px;
  }
}
</style>
