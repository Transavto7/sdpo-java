<script>
import Loader from "@/components/common/Loader";
import {useToast} from "vue-toastification";
import {printQr} from "@/helpers/printer";
import PrintQrLogin from "@/pages/print/QR/PrintQrLogin";
import SelectQr from "@/pages/print/QR/SelectQr";


export default {
  components: {SelectQr, PrintQrLogin, Loader},
  data() {
    return {
      toast: useToast(),
      loading: false,
      visibleNumPad: false,
      selectTab: ""
    }
  },
  methods: {
    print(hash_id) {
      printQr(hash_id,  this.selectTab);
      this.toast.success('Задание на печать отправлено');
      setTimeout(() => {
        this.$router.push('/');
      }, 5000);
    },
    setTab(type) {
      this.showNumPad()
      this.selectTab = type
    },
    showNumPad() {
      this.visibleNumPad = true;
    },
  },
  computed: {
    login() {
      return this.driver !== null
    },
  },
}
</script>

<template>
  <print-qr-login v-if="this.visibleNumPad"
                  :type="this.selectTab"
                  @success-auth="(hash_id) => print(hash_id)"
  />
  <select-qr v-if=" !this.visibleNumPad"
                  @select="(type) => setTab(type)"
  />
</template>
