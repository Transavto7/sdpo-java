<script>
import {close} from '@/helpers/api/api';
import AdminNavigation from "@/components/navigation/common/AdminNavigation";
import LoginNavigation from "@/components/navigation/common/LoginNavigation";
import InspectionProgressLine from "@/components/navigation/common/InspectionProgressLine";

export default {
  components: {InspectionProgressLine, LoginNavigation, AdminNavigation},
  data() {
    return {}
  },
  methods: {
    close,
    changeVisibleMouseCursor() {
      if (!document.documentElement.classList.contains('disable-mouse')) {
        document.documentElement.classList.add('disable-mouse');
        this.system.cursor = false;
      } else {
        document.documentElement.classList.remove('disable-mouse');
        this.system.cursor = true;
      }
    },
    logout() {
      this.$store.state.admin = false;
      this.$router.push('/');
    },
    changeModeFullScreen() {
      const isInFullScreen =
          document.fullscreenElement ||
          document.webkitFullscreenElement ||
          document.mozFullScreenElement ||
          document.msFullscreenElement;

      const docElm = document.documentElement;
      if (!isInFullScreen) {
        if (docElm.requestFullscreen) {
          docElm.requestFullscreen();
        } else if (docElm.mozRequestFullScreen) {
          docElm.mozRequestFullScreen();
        } else if (docElm.webkitRequestFullScreen) {
          docElm.webkitRequestFullScreen();
        } else if (docElm.msRequestFullscreen) {
          docElm.msRequestFullscreen();
        }
      } else {
        if (document.exitFullscreen) {
          document.exitFullscreen();
        } else if (document.webkitExitFullscreen) {
          document.webkitExitFullscreen();
        } else if (document.mozCancelFullScreen) {
          document.mozCancelFullScreen();
        } else if (document.msExitFullscreen) {
          document.msExitFullscreen();
        }
      }
    },
  },
  computed: {
    needButtonRedirectOnHomePage() {
      return this.isHelpPage || this.isInspectionPage || this.isLoginPage || this.isPrintPage || this.isSetNumberPhonePage;
    },
    isHomePage() {
      return !(this.isHelpPage || this.isInspectionPage || this.isLoginPage || this.isAdminPage || this.isPrintPage || this.isSetNumberPhonePage);
    },
    isHelpPage() {
      return this.currentRouter.includes('/help');
    },
    isLoginPage() {
      return this.currentRouter.includes('/login');
    },
    isAdminPage() {
      return this.currentRouter.includes('/settings');
    },
    isInspectionPage() {
      return this.currentRouter.includes('/step');
    },
    isPrintPage() {
      return this.currentRouter.includes('/print');
    },
    isSetNumberPhonePage() {
      return this.currentRouter.includes('/number-phone/add/');
    },
    currentRouter() {
      return this.$route.path;
    },
    inspection() {
      return this.$store.state.inspection;
    },
    logo() {
      return this.$store.state.config?.main?.logo;
    }
  },
}
</script>

<template>
  <div class="nav">
    <div class="nav__logo animate__animated animate__fadeInDown">
      <img v-if="logo" :src="logo" alt="">
      <img v-else src="@/assets/images/logo.png" alt="">
    </div>

    <div v-if="isInspectionPage" class="nav__info animate__animated animate__fadeInDown">
      {{ inspection.driver_fio }}
      <span>{{ inspection.driver_id }}</span>
    </div>

    <div class="nav__buttons" v-if="needButtonRedirectOnHomePage">
      <button @click="$router.push('/')" class="btn opacity blue animate__animated animate__fadeInDown d-1">В начало
      </button>
    </div>

    <div class="nav__buttons" v-if="isAdminPage">
      <admin-navigation @change-visible-mouse-cursor="changeVisibleMouseCursor()"
                        @changeModeFullScreen="changeModeFullScreen()"
                        @logout="logout()"/>
    </div>
    <div class="nav__buttons" v-if="isHomePage">
      <login-navigation @select-medic="$store.state.selectingMedic = true"
                        @get-help="$router.push('/help')"
                        @get-last-inspection="$router.push('/print/index')"
                        @print-qr="$router.push('/print/qr')"
                        @settings="$router.push('/login')"/>
    </div>
  </div>
  <div class="step-progress animate__animated animate__fadeInDown" v-if="isInspectionPage">
    <InspectionProgressLine/>
  </div>
</template>`