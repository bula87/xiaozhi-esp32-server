<template>
  <div class="page-container">
    <div class="operation-bar">
      <h2 class="page-title">{{ $t("header.providerManagement") }}</h2>
      <div class="right-operations">
        <el-dropdown
          trigger="click"
          @command="handleSelectModelType"
          @visible-change="handleDropdownVisibleChange"
        >
          <el-button class="category-btn" size="mini">
            {{ $t("providerManagement.categoryFilter") }}
            {{ selectedModelTypeLabel }}
            <i class="el-icon-arrow-down el-icon--right" :class="{ 'rotate-down': DropdownVisible }"></i>
          </el-button>
          <el-dropdown-menu slot="dropdown" class="aurora-dropdown">
            <el-dropdown-item command="">{{ $t("common.all") }}</el-dropdown-item>
            <el-dropdown-item
              v-for="item in translatedModelTypes"
              :key="item.value"
              :command="item.value"
            >
              {{ item.label }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>

        <el-input
          v-model="searchName"
          :placeholder="$t('providerManagement.searchPlaceholder')"
          class="search-input"
          clearable
          @keyup.enter.native="handleSearch"
        />
        <el-button class="btn-search" @click="handleSearch">
          {{ $t("common.search") }}
        </el-button>
      </div>
    </div>

    <div class="main-wrapper">
      <div class="content-panel">
        <div class="content-area">
          <el-card class="provider-card" shadow="never">
            <el-table
              ref="providersTable"
              :data="filteredProvidersList"
              class="transparent-table"
              v-loading="loading"
              height="100%"
              :element-loading-text="$t('common.loading')"
              element-loading-spinner="el-icon-loading"
              element-loading-background="rgba(11, 15, 25, 0.8)"
              :header-cell-class-name="headerCellClassName"
            >
              <el-table-column align="center" width="80">
                <template slot="header">
                  <span class="selection-header-text">{{ $t('modelConfig.select') }}</span>
                </template>
                <template slot-scope="scope">
                  <el-checkbox v-model="scope.row.selected"></el-checkbox>
                </template>
              </el-table-column>

              <el-table-column :label="$t('providerManagement.category')" prop="modelType" align="center" width="150">
                <template slot-scope="scope">
                  <el-tag :type="getModelTypeTag(scope.row.modelType)" size="mini" effect="dark" class="aurora-tag">
                    {{ getModelTypeLabel(scope.row.modelType) }}
                  </el-tag>
                </template>
              </el-table-column>

              <el-table-column :label="$t('providerManagement.providerCode')" prop="providerCode" align="center" width="150" />
              <el-table-column :label="$t('common.name')" prop="name" align="center" />

              <el-table-column :label="$t('providerManagement.fieldConfig')" align="center">
                <template slot-scope="scope">
                  <el-popover placement="top-start" width="400" trigger="hover" popper-class="aurora-popover">
                    <div v-for="field in scope.row.fields" :key="field.key" class="field-item">
                      <span class="field-label">{{ field.label }}:</span>
                      <span class="field-type">{{ field.type }}</span>
                      <span v-if="isSensitiveField(field.key)" class="sensitive-tag">
                        {{ $t("common.sensitive") }}
                      </span>
                    </div>
                    <el-button slot="reference" size="mini" type="text" class="view-fields-btn">
                      {{ $t("providerManagement.viewFields") }}
                    </el-button>
                  </el-popover>
                </template>
              </el-table-column>

              <el-table-column :label="$t('common.sort')" prop="sort" align="center" width="80" />

              <el-table-column :label="$t('common.action')" align="center" width="180">
                <template slot-scope="scope">
                  <el-button type="text" size="mini" @click="editProvider(scope.row)">{{ $t("common.edit") }}</el-button>
                  <el-button type="text" size="mini" class="delete-btn" @click="deleteProvider(scope.row)">{{ $t("common.delete") }}</el-button>
                </template>
              </el-table-column>
            </el-table>

            <div class="table_bottom">
              <div class="ctrl_btn">
                <el-button type="primary" size="mini" @click="handleSelectAll">
                  {{ isAllSelected ? $t("common.deselectAll") : $t("common.selectAll") }}
                </el-button>
                <el-button type="success" size="mini" @click="showAddDialog">{{ $t("common.add") }}</el-button>
                <el-button type="danger" size="mini" :disabled="selectedModelsCount === 0" @click="deleteSelectedProviders">
                  {{ $t("common.delete") }}
                </el-button>
              </div>

              <div class="custom-pagination">
                <el-pagination
                  background
                  layout="total, sizes, prev, pager, next"
                  :current-page.sync="currentPage"
                  :page-size.sync="pageSize"
                  :total="total"
                  :page-sizes="pageSizeOptions"
                  @size-change="handlePageSizeChange"
                  @current-change="fetchProviders"
                />
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <provider-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      :form="providerForm"
      :model-types="modelTypes"
      @submit="handleSubmit"
      @cancel="dialogVisible = false"
    />

    <div class="footer-container">
      <version-footer />
    </div>
  </div>
</template>

<script>
import Api from "@/apis/api";
import ProviderDialog from "@/components/ProviderDialog.vue";
import VersionFooter from "@/components/VersionFooter.vue";

export default {
  name: "ProviderManagement",
  components: { ProviderDialog, VersionFooter },
  data() {
    return {
      searchName: "",
      searchModelType: "",
      providersList: [],
      modelTypes: [
        { value: "ASR", labelKey: "providerManagement.modelType.ASR" },
        { value: "TTS", labelKey: "providerManagement.modelType.TTS" },
        { value: "LLM", labelKey: "providerManagement.modelType.LLM" },
        { value: "VLLM", labelKey: "providerManagement.modelType.VLLM" },
        { value: "Intent", labelKey: "providerManagement.modelType.Intent" },
        { value: "Memory", labelKey: "providerManagement.modelType.Memory" },
        { value: "VAD", labelKey: "providerManagement.modelType.VAD" },
        { value: "Plugin", labelKey: "providerManagement.modelType.Plugin" },
        { value: "RAG", labelKey: "providerManagement.modelType.RAG" },
      ],
      currentPage: 1,
      loading: false,
      pageSize: 10,
      pageSizeOptions: [10, 20, 50, 100],
      total: 0,
      dialogVisible: false,
      dialogTitle: "Add Provider",
      isAllSelected: false,
      sensitive_keys: ["api_key", "token", "secret", "password"],
      providerForm: {
        id: null,
        modelType: "",
        providerCode: "",
        name: "",
        fields: [],
        sort: 0,
      },
      DropdownVisible: false,
    };
  },
  created() {
    this.fetchProviders();
  },
  computed: {
    translatedModelTypes() {
      return this.modelTypes.map((type) => ({
        value: type.value,
        label: this.$t(type.labelKey),
      }));
    },
    selectedModelTypeLabel() {
      if (!this.searchModelType) return `(${this.$t("common.all")})`;
      const selectedType = this.modelTypes.find(item => item.value === this.searchModelType);
      return selectedType ? `(${this.$t(selectedType.labelKey)})` : "";
    },
    filteredProvidersList() {
      return this.providersList;
    },
    selectedModelsCount() {
      return this.providersList.filter(row => row.selected).length;
    }
  },
  methods: {
    fetchProviders() {
      this.loading = true;
      Api.model.getModelProvidersPage({
        page: this.currentPage,
        limit: this.pageSize,
        name: this.searchName,
        modelType: this.searchModelType,
      }, ({ data }) => {
        this.loading = false;
        if (data.code === 0) {
          this.providersList = data.data.list.map(item => ({
            ...item,
            selected: false,
            fields: item.fields ? JSON.parse(item.fields) : [],
          }));
          this.total = data.data.total;
        }
      });
    },
    handleSearch() {
      this.currentPage = 1;
      this.fetchProviders();
    },
    handleSelectModelType(value) {
      this.searchModelType = value;
      this.handleSearch();
    },
    handleSelectAll() {
      this.isAllSelected = !this.isAllSelected;
      this.providersList.forEach(row => (row.selected = this.isAllSelected));
    },
    showAddDialog() {
      this.dialogTitle = this.$t("common.addProvider");
      this.providerForm = { id: null, modelType: "", providerCode: "", name: "", fields: [], sort: 0 };
      this.dialogVisible = true;
    },
    editProvider(row) {
      this.dialogTitle = this.$t("common.editProvider");
      this.providerForm = { ...row, fields: JSON.parse(JSON.stringify(row.fields)) };
      this.dialogVisible = true;
    },
    handleSubmit({ form, done }) {
      const apiMethod = form.id ? Api.model.updateModelProvider : Api.model.addModelProvider;
      apiMethod(form, ({ data }) => {
        if (data.code === 0) {
          this.$message.success(this.$t("common.success"));
          this.fetchProviders();
          this.dialogVisible = false;
        }
        done && done();
      });
    },
    deleteSelectedProviders() {
      const selected = this.providersList.filter(row => row.selected);
      this.deleteProvider(selected);
    },
    deleteProvider(row) {
      const providers = Array.isArray(row) ? row : [row];
      this.$confirm(this.$t("common.confirmDelete"), this.$t("common.warning"), { type: "warning" }).then(() => {
        const ids = providers.map(p => p.id);
        Api.model.deleteModelProviderByIds(ids, ({ data }) => {
          if (data.code === 0) {
            this.$message.success(this.$t("common.deleteSuccess"));
            this.fetchProviders();
          }
        });
      });
    },
    getModelTypeTag(type) {
      const map = { ASR: "success", TTS: "warning", LLM: "danger", Intent: "info", VAD: "primary" };
      return map[type] || "";
    },
    getModelTypeLabel(type) {
      const item = this.modelTypes.find(i => i.value === type);
      return item ? this.$t(item.labelKey) : type;
    },
    isSensitiveField(key) {
      return this.sensitive_keys.some(k => key.toLowerCase().includes(k));
    },
    handlePageSizeChange(val) {
      this.pageSize = val;
      this.currentPage = 1;
      this.fetchProviders();
    },
    handleDropdownVisibleChange(v) { this.DropdownVisible = v; },
    headerCellClassName({ columnIndex }) {
      return columnIndex === 0 ? "custom-selection-header" : "";
    }
  }
};
</script>
<style lang="scss" scoped>
@import "../styles/aurora-theme.scss";

/* --- Unique styles for ProviderManagement --- */
.provider-name-text { color: $accent-cyan; font-weight: bold; }
</style>
