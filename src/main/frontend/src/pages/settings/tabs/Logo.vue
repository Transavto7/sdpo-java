<script>
import { saveLogo } from '@/helpers/api/api';
import { useToast } from "vue-toastification";

export default {
    data() {
        return {
            toast: useToast(),
        }
    },
    methods: {
        async onFileChange(e) {
            const file = e.target.files[0];
            const logo = await this.getBase64(file);
            this.$store.state.config.main.logo = logo;
            await saveLogo(logo);
            this.toast.success('Логотип успешно установлен');
        },
        getBase64(file) {
            return new Promise((resolve, reject) => {
                const reader = new FileReader();
                reader.readAsDataURL(file);
                reader.onload = () => {
                    resolve(reader.result);
                };
                reader.onerror = (error) => {
                    console.log('Error: ', error);
                };
            })
        },
        async deleteLogo() {
            this.$store.state.config.main.logo = "";
            await saveLogo("");
            this.toast.success('Логотип успешно удален');
        }
    },
    computed: {
        logo() {
            return this.$store.state.config?.main?.logo;
        }
    }
}
</script>

<template>
    <div class="admin__logo">
        <label class="input-file">
            <input type="file" @change="onFileChange">		
            <span>Выберите файл</span>
        </label>
        <div v-if="logo" class="preview-image">
            <img :src="logo">
            <button @click="deleteLogo" class="btn small">Удалить</button>
        </div>
    </div>
</template>