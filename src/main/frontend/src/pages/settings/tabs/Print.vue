<script>

import {getStamps, saveStamp} from "@/helpers/api";
import {useToast} from "vue-toastification";

export default {
  name: "Print",
  async mounted() {
    this.selectedStamp = await this.getStampFromLocalStorage();
    this.stamps = await getStamps();
  },
  data() {
    return {
      toast: useToast(),
      stamps: [],
      selectedStamp: null,
    }
  },
  methods: {
    async getStampFromLocalStorage() {
      return this.$store.state.config?.main?.selected_stamp ?? null;
    },
    selectStamp(stamp) {
      this.selectedStamp = stamp
    },
    setStampLocalStorage(stamp) {
      this.$store.state.config.main.selected_stamp = {
        stamp_head: stamp.stamp_head,
        stamp_licence: stamp.stamp_licence,
      }
    },
    stampEquals(stamp) {
      if (this.selectedStamp === null) return false;
      return stamp.stamp_licence === this.selectedStamp.stamp_licence;
    },
    async save() {
      if (this.isSelectedStamp) {
        await saveStamp(this.selectedStamp);
        this.setStampLocalStorage(this.selectedStamp)
        this.toast.success('Настройки сохранены');
      } else
      {
        this.toast.error('Штамп не выбран');
      }
    },
    formatNumeric(num) {
      return num < 10 ? '0' + num : num;
    }

  },
  computed: {
    isSelectedStamp() {
      return this.selectedStamp !== null;
    },
    medic() {
      return this.$store.state.config?.main?.selected_medic || {};
    },
    date() {
      const date = new Date();
      return date.getFullYear() + '-' +
          this.formatNumeric(date.getMonth()) + '-' +
          this.formatNumeric(date.getDay()) + ' ' +
          this.formatNumeric(date.getHours()) + ':' +
          this.formatNumeric(date.getMinutes()) + ':' +
          this.formatNumeric(date.getSeconds());
    },
    validity_period_eds() {
      if (this.medic.validity_eds_start && this.medic.validity_eds_end) {
        return "Срок действия с " + this.medic.validity_eds_start + ' по ' + this.medic.validity_eds_end
      }
      return '';
    }
  },
}
</script>

<template>
  <div>
    <div class="stamp animate__animated animate__fadeInUp">
      <div class="stamp__body">
        <div class="template">
          <div class="template__header">
            <h2>Предпросмотр печати</h2>
          </div>
          <div class="template__content">
            <span class="header">{{ isSelectedStamp ? selectedStamp.stamp_head : `ООО "Трансавто-7"` }}</span><br>
            <span class="license">{{ isSelectedStamp ? selectedStamp.stamp_licence : `` }}</span><br>
            <span>Иванов Иван</span><br>
            <span>прошел Предрейсовый/Предсменный</span><br>
            <span>медицинский осмотр</span><br>
            <span>к исполнению трудовых обязаностей </span><br>
            <span>допущен</span><br>
            <span></span><br>
            <span>{{date}}</span><br>
            <span>{{medic.name || ''}}</span><br>
            <span>ЭЦП {{medic.eds || ''}}</span><br>
            <span>{{validity_period_eds}}</span><br>
          </div>
          <div v-if="this.isSelectedStamp" class="animate__animated animate__fadeInUp d-7" style="margin-top: 15px; width: 100%;">
            <button @click="save" class="btn blue" style="width: 100%;">Сохранить</button>
          </div>
        </div>
        <div class="stampBox">
          <div class="stampBox__header">
            <h2>Выбор штампа</h2>
          </div>
          <div class="stampBox__content">
            <div class="stampItem"
                 v-for="stamp in this.stamps"
                 @click="selectStamp(stamp[0])"
                 :class="{selected : stampEquals(stamp[0])}">
              <span class="header">{{ (stamp)[0].stamp_head }}</span><br>
              <span class="license">{{ (stamp)[0].stamp_licence }}</span><br>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>


<style scoped>

</style>