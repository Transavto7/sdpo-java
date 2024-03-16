<script>
import PrintLogin from "@/pages/print/PrintLogin";
import PrintIndex from "@/pages/print/PrintIndex";
import Loader from "@/components/common/Loader";
import {getInspections, printInspection} from '@/helpers/api';


export default {
  components: {Loader, PrintIndex, PrintLogin},
  data() {
    return {
      driver: null,
      inspection: {},
      inspections: {},
      print: false,
      errorAuthentication: false,
      loading: false,
    }
  },
  methods: {
    async confirmPrint() {
      await printInspection(this.inspection)
      this.$router.push('/');

    },
    printQuery(inspectionAttr) {
      this.inspection = inspectionAttr
      this.confirmPrint()
    },
    async setDriver(driver) {
      this.loading = true
      this.inspections = await getInspections(driver.hash_id);
      this.loading = false;
      this.driver = driver;
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
  <print-login v-if="!this.login" @success-auth="(driver) => setDriver(driver)"/>
  <print-index v-if="this.login"
               @print="(inspectionAttr) => printQuery(inspectionAttr)"
               v-model:inspections="inspections"
               v-model:driver="driver"/>
  <!--//todo доавить модалку с ожиданием печати-->
  <!--//todo добавить обработку ошибок-->
  <loader v-model:loading="loading"/>
</template>
