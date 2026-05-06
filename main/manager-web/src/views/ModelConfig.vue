<template>
  <div class="page-container">
    <div class="operation-bar">
      <h2 class="page-title">{{ layoutTitle }}</h2>
      <div class="right-operations">
        <template v-if="activeTab !== 'overview'">
          <el-input
            v-model="search"
            :placeholder="$t('modelConfig.searchPlaceholder')"
            class="search-input"
            clearable
            @keyup.enter.native="handleSearch"
          />
          <el-button class="btn-search" @click="handleSearch">
            {{ $t("common.search") }}
          </el-button>
        </template>
      </div>
    </div>

    <div class="main-wrapper">
      <div class="content-panel">
        <div class="content-area">
          
          <div 
            v-if="activeTab === 'overview'" 
            class="model-overview" 
            v-loading="overviewLoading"
            :element-loading-text="$t('common.loading')"
            element-loading-spinner="el-icon-loading"
            element-loading-background="rgba(11, 15, 25, 0.8)"
          >
            <p class="overview-lead">{{ $t("modelConfig.overviewLead") }}</p>
            <div class="overview-grid">
              <button
                v-for="row in overviewRows"
                :key="row.key"
                type="button"
                class="overview-tile"
                @click="goOverviewCategory(row.key)"
              >
                <div class="overview-tile-title">{{ $t(modelTabI18n[row.key]) }}</div>
                <div class="overview-tile-model">{{ row.label }}</div>
              </button>
            </div>
          </div>

          <el-card v-else class="template-card" shadow="never">
            <el-table
              ref="modelTable"
              :data="modelList"
              v-loading="loading"
              class="transparent-table"
              height="100%"
              :element-loading-text="$t('common.loading')"
              element-loading-spinner="el-icon-loading"
              element-loading-background="rgba(11, 15, 25, 0.8)"
              @selection-change="handleSelectionChange"
            >
              <el-table-column type="selection" width="55" align="center" />
              <el-table-column :label="$t('modelConfig.modelId')" prop="id" align="center" />
              <el-table-column :label="$t('modelConfig.modelName')" prop="modelName" align="center" />
              
              <el-table-column :label="$t('modelConfig.provider')" align="center">
                <template slot-scope="scope">
                  <span class="provider-text">{{ scope.row.configJson.type || $t("modelConfig.unknown") }}</span>
                </template>
              </el-table-column>

              <el-table-column :label="$t('modelConfig.isEnabled')" align="center">
                <template slot-scope="scope">
                  <el-switch
                    v-model="scope.row.isEnabled"
                    class="aurora-switch"
                    :active-value="1"
                    :inactive-value="0"
                    :disabled="scope.row.isDefault === 1"
                    @change="handleStatusChange(scope.row)"
                  />
                </template>
              </el-table-column>

              <el-table-column :label="$t('modelConfig.isDefault')" align="center">
                <template slot-scope="scope">
                  <el-switch
                    v-model="scope.row.isDefault"
                    class="aurora-switch"
                    :active-value="1"
                    :inactive-value="0"
                    @change="handleDefaultChange(scope.row)"
                  />
                </template>
              </el-table-column>

              <el-table-column :label="$t('modelConfig.action')" align="center" width="240px">
                <template slot-scope="scope">
                  <el-button type="text" size="mini" @click="editModel(scope.row)">{{ $t("modelConfig.edit") }}</el-button>
                  <el-button type="text" size="mini" @click="duplicateModel(scope.row)">{{ $t("modelConfig.duplicate") }}</el-button>
                  <el-button type="text" size="mini" class="delete-btn" @click="deleteModel(scope.row)">{{ $t("modelConfig.delete") }}</el-button>
                </template>
              </el-table-column>
            </el-table>

            <div class="table_bottom">
              <div class="ctrl_btn">
                <el-button type="primary" size="mini" @click="selectAll">
                  {{ isAllSelected ? $t("modelConfig.deselectAll") : $t("modelConfig.selectAll") }}
                </el-button>
                <el-button type="success" size="mini" @click="addModel">{{ $t("modelConfig.add") }}</el-button>
                <el-button type="danger" size="mini" :disabled="selectedModels.length === 0" @click="batchDelete">
                   {{ $t("modelConfig.delete") }}
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
                  @current-change="loadData"
                />
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <ModelEditDialog :visible.sync="editDialogVisible" :modelData="editModelData" @save="handleModelSave" />
    <AddModelDialog :visible.sync="addDialogVisible" :modelType="dialogModelType" @confirm="handleAddConfirm" />
    <TtsModel :visible.sync="ttsDialogVisible" :ttsModelId="selectedTtsModelId" :modelConfig="selectedModelConfig" />
    
    <div class="footer-container">
       <version-footer />
    </div>
  </div>
</template>

<script>
import Api from "@/apis/api";
import AddModelDialog from "@/components/AddModelDialog.vue";
import ModelEditDialog from "@/components/ModelEditDialog.vue";
import TtsModel from "@/components/TtsModel.vue";
import VersionFooter from "@/components/VersionFooter.vue";

const MODEL_TAB_KEYS = [
  "overview",
  "vad",
  "asr",
  "llm",
  "vllm",
  "intent",
  "tts",
  "memory",
  "rag",
];

const MODEL_TAB_I18N = {
  overview: "modelConfig.overview",
  vad: "modelConfig.vad",
  asr: "modelConfig.asr",
  llm: "modelConfig.llm",
  vllm: "modelConfig.vllm",
  intent: "modelConfig.intent",
  tts: "modelConfig.tts",
  memory: "modelConfig.memory",
  rag: "modelConfig.rag",
};

const MODEL_CATEGORY_KEYS = MODEL_TAB_KEYS.filter((k) => k !== "overview");

export default {
  name: 'ModelConfig', // Added the name property
  components: { ModelEditDialog, TtsModel, AddModelDialog, VersionFooter },
  data() {
    return {
      addDialogVisible: false,
      activeTab: "overview",
      modelTabI18n: MODEL_TAB_I18N,
      overviewRows: [],
      overviewLoading: false,
      search: "",
      editDialogVisible: false,
      editModelData: {},
      ttsDialogVisible: false,
      selectedTtsModelId: "",
      modelList: [],
      pageSizeOptions: [10, 20, 50, 100],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      selectedModels: [],
      isAllSelected: false,
      loading: false,
      selectedModelConfig: {},
    };
  },
  watch: {
    $route: {
      handler(to) {
        if (to.path === "/model-config") {
          this.applyRouteTab();
        }
      },
      immediate: true,
    },
  },
  computed: {
    layoutTitle() {
      if (this.activeTab === "overview") return this.$t("modelConfig.overview");
      return this.$t("modelConfig." + this.activeTab);
    },
    dialogModelType() {
      return this.activeTab === "overview" ? "llm" : this.activeTab;
    },
  },
  methods: {
    handlePageSizeChange(val) {
      this.pageSize = val;
      this.currentPage = 1;
      this.loadData();
    },
    applyRouteTab() {
      const tab = this.$route.query.tab || "overview";
      this.activeTab = tab;
      if (tab === "overview") this.loadOverviewDefaults();
      else this.loadData();
    },
    loadOverviewDefaults() {
      this.overviewLoading = true;
      const tasks = MODEL_CATEGORY_KEYS.map(modelType => 
        new Promise(resolve => {
          Api.model.getModelList({ modelType, page: 1, limit: 200 }, ({ data }) => {
            const list = data.data?.list || [];
            const pick = list.find(m => m.isDefault === 1) || list[0];
            resolve({ key: modelType, label: pick ? pick.modelName : "—" });
          }, () => resolve({ key: modelType, label: "—" }));
        })
      );
      Promise.all(tasks).then(rows => {
        this.overviewRows = rows;
        this.overviewLoading = false;
      });
    },
    loadData() {
      if (this.activeTab === "overview") return;
      this.loading = true;
      Api.model.getModelList({
        modelType: this.activeTab,
        modelName: this.search,
        page: this.currentPage,
        limit: this.pageSize,
      }, ({ data }) => {
        this.loading = false;
        if (data.code === 0) {
          this.modelList = data.data.list;
          this.total = data.data.total;
        }
      });
    },
    handleSearch() {
      this.currentPage = 1;
      this.loadData();
    },
    selectAll() {
      this.isAllSelected = !this.isAllSelected;
      if (this.$refs.modelTable) {
        this.$refs.modelTable.toggleAllSelection();
      }
    },
    handleSelectionChange(val) {
      this.selectedModels = val;
      this.isAllSelected = val.length > 0 && val.length === this.modelList.length;
    },
    editModel(model) {
      this.editModelData = JSON.parse(JSON.stringify(model));
      this.editDialogVisible = true;
    },
    duplicateModel(model) {
      this.editModelData = JSON.parse(JSON.stringify(model));
      this.editModelData.duplicateMode = true;
      this.editDialogVisible = true;
    },
    deleteModel(model) {
      this.$confirm(this.$t("modelConfig.confirmDelete"), "Warning", { type: "warning" }).then(() => {
        Api.model.deleteModel(model.id, () => this.loadData());
      });
    },
    batchDelete() {
      this.$confirm(this.$t("modelConfig.confirmBatchDelete"), "Warning", { type: "warning" }).then(() => {
        const promises = this.selectedModels.map(m => new Promise(res => Api.model.deleteModel(m.id, res)));
        Promise.all(promises).then(() => this.loadData());
      });
    },
    addModel() { this.addDialogVisible = true; },
    handleStatusChange(model) {
      const status = model.isEnabled ? 1 : 0;
      Api.model.updateModelStatus(model.id, status, () => this.loadData());
    },
    handleDefaultChange(model) {
      Api.model.setDefaultModel(model.id, () => this.loadData());
    },
    goOverviewCategory(key) {
      this.$router.push({ path: "/model-config", query: { tab: key } });
    },
    // Adding placeholder for the save event emitted by dialogs
    handleModelSave() {
        this.loadData();
        this.editDialogVisible = false;
    },
    handleAddConfirm() {
        this.loadData();
        this.addDialogVisible = false;
    }
  }
};
</script>
<style lang="scss" scoped>
@import "../styles/aurora-theme.scss";

/* --- Unique styles for ModelConfig --- */
.provider-text { color: $accent-purple; }
</style>
