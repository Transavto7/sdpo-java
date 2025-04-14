<script>

import {useToast} from "vue-toastification";
import {
  getEmployeesInspectionFromLocalStorage,
} from "@/helpers/api/inspection/inspections";

export default {
  name: "QueueToSend",
  async mounted() {
    this.inspections = await getEmployeesInspectionFromLocalStorage();
  },
  data() {
    return {
      toast: useToast(),
      inspections: [
      ],
    }
  },
  methods: {
    translateTypeAnketa(type) {
      return type === 'open' ? 'Открытие' : 'Закрытие';
    }
  },
  computed: {
    system() {
      return this.$store.state.config?.system || {};
    },
  }
}
</script>

<template>
  <div>
    <div class="stamp animate__animated animate__fadeInUp">
      <div class="stamp__body">
        <div class="queue-to-send-box">
          <div class="queue-to-send-box__header">
            <h2>Очередь на отправку</h2>
          </div>
          <div class="queue-to-send-box__content">
            <div class="queue-item"
                 v-for="(inspection) in this.inspections"
                 :class="{selected : false}">
              <p class="header">{{ inspection.person_fio }}</p>
              <p class="">{{ inspection.person_id }}</p>
              <p class="">{{ translateTypeAnketa(inspection.type_anketa) }}</p>
              <p class="">{{ inspection.created_at }}</p>
            </div>
            <div class="queue-item no-inspections" v-if="inspections.length === 0"
                 :class="{selected : false}">
              <p class="">Нет осмотров для отправки</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>


<style scoped>
.no-inspections {
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>