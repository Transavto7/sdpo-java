<script>
import PrintLogin from "@/pages/print/PrintLogin";
import PrintIndex from "@/pages/print/PrintIndex";
import Loader from "@/components/common/Loader";
import {getInspections, printInspection} from '@/helpers/api';
import {useToast} from "vue-toastification";


export default {
  components: {Loader, PrintIndex, PrintLogin},
  data() {
    return {
      driver: null,
      toast: useToast(),
      inspections: {},
      print: false,
      errorAuthentication: false,
      loading: false,
    }
  },
  methods: {
    async confirmPrint(inspection) {
      await printInspection(inspection)
      this.toast.success('Задание на печать отправлено');
      this.$router.push('/');

    },
    printQuery(inspectionAttr) {
      this.confirmPrint(inspectionAttr)
    },
    async setDriver(driver) {
      this.loading = true
      this.inspections = await getInspections(driver.hash_id);
      this.driver = driver;
      this.loading = false;

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
  <print-login v-if="!this.login && !this.loading" @success-auth="(driver) => setDriver(driver)"/>
  <print-index v-if="this.login && !this.loading"
               @print="(inspectionAttr) => printQuery(inspectionAttr)"
               v-model:inspections="inspections"
               v-model:driver="driver"/>
  <!--//todo доавить модалку с ожиданием печати-->
  <!--//todo добавить обработку ошибок-->
  <loader v-model:loading="loading"/>
</template>
