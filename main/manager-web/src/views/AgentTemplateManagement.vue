<template>
  <div class="page-container">
    <div class="operation-bar">
      <h2 class="page-title">{{ $t("agentTemplateManagement.title") }}</h2>
      <div class="right-operations">
        <el-input
          v-model="search"
          :placeholder="$t('agentTemplateManagement.searchPlaceholder')"
          class="search-input"
          clearable
          @keyup.enter.native="handleSearch"
        />
        <el-button class="btn-search" @click="handleSearch">
          {{ $t("agentTemplateManagement.search") }}
        </el-button>
      </div>
    </div>

    <div class="main-wrapper">
      <div class="content-panel">
        <div class="content-area">
          <el-card class="template-card" shadow="never">
            <el-table
              ref="templateTable"
              :data="templateList"
              v-loading="templateLoading"
              class="transparent-table"
              height="100%"
              :element-loading-text="$t('common.loading')"
              element-loading-spinner="el-icon-loading"
              element-loading-background="rgba(11, 15, 25, 0.8)"
            >
              <el-table-column align="center" width="100">
                <template slot="header">
                  <span class="selection-header-text">{{ $t('agentTemplateManagement.select') }}</span>
                </template>
                <template slot-scope="scope">
                  <el-checkbox
                    v-model="scope.row.selected"
                    @change="handleRowSelectionChange(scope.row)"
                    @click.stop
                  ></el-checkbox>
                </template>
              </el-table-column>

              <el-table-column
                :label="$t('agentTemplateManagement.serialNumber')"
                width="120"
                align="center"
              >
                <template slot-scope="scope">
                  <span class="serial-text">{{ (currentPage - 1) * pageSize + scope.$index + 1 }}</span>
                </template>
              </el-table-column>

              <el-table-column
                :label="$t('agentTemplateManagement.templateName')"
                prop="agentName"
                min-width="250"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <span class="template-name-text">{{ scope.row.agentName }}</span>
                </template>
              </el-table-column>

              <el-table-column
                :label="$t('agentTemplateManagement.action')"
                min-width="250"
                align="center"
              >
                <template slot-scope="scope">
                  <div class="action-cell">
                    <el-button type="text" size="mini" @click="editTemplate(scope.row)">
                      {{ $t("agentTemplateManagement.editTemplate") }}
                    </el-button>
                    <el-button type="text" size="mini" class="delete-btn" @click="deleteTemplate(scope.row)">
                      {{ $t("agentTemplateManagement.deleteTemplate") }}
                    </el-button>
                  </div>
                </template>
              </el-table-column>
            </el-table>

            <div class="table_bottom">
              <div class="ctrl_btn">
                <el-button
                  type="primary"
                  size="mini"
                  @click="handleSelectAll"
                >
                  {{ isAllSelected ? $t("agentTemplateManagement.deselectAll") : $t("agentTemplateManagement.selectAll") }}
                </el-button>
                <el-button type="success" size="mini" @click="showAddTemplateDialog">
                  {{ $t("agentTemplateManagement.createTemplate") }}
                </el-button>
                <el-button
                  type="danger"
                  size="mini"
                  :disabled="!hasSelected"
                  @click="batchDeleteTemplate"
                >
                  {{ $t("agentTemplateManagement.batchDelete") }}
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
                  @current-change="handlePageChange"
                />
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <div class="footer-container">
      <version-footer />
    </div>
  </div>
</template>

<script>
import agentApi from "@/apis/module/agent";
import VersionFooter from "@/components/VersionFooter.vue";

export default {
  name: "AgentTemplateManagement",
  components: { VersionFooter },
  data() {
    return {
      templateList: [],
      templateLoading: false,
      selectedTemplates: [],
      isAllSelected: false,
      search: "",
      pageSizeOptions: [10, 20, 50, 100],
      currentPage: 1,
      pageSize: 10,
      total: 0,
    };
  },
  computed: {
    hasSelected() {
      return this.selectedTemplates.length > 0;
    },
  },
  created() {
    this.loadTemplateList();
  },
  methods: {
    loadTemplateList() {
      this.templateLoading = true;
      const params = { page: this.currentPage, limit: this.pageSize };
      if (this.search) params.agentName = this.search;

      agentApi.getAgentTemplatesPage(params, (res) => {
        if (res?.data?.code === 0) {
          const responseData = res.data.data || {};
          this.templateList = (responseData.list || []).map((item) => ({ ...item, selected: false }));
          this.total = responseData.total || 0;
        }
        this.templateLoading = false;
      }, () => {
        this.templateLoading = false;
      });
    },
    handleSearch() {
      this.currentPage = 1;
      this.loadTemplateList();
    },
    showAddTemplateDialog() {
      this.$router.push({ path: "/template-quick-config" });
    },
    editTemplate(row) {
      this.$router.push({ path: "/template-quick-config", query: { templateId: row.id } });
    },
    deleteTemplate(row) {
      this.$confirm(this.$t("agentTemplateManagement.confirmSingleDelete"), "Warning", { type: "warning" }).then(() => {
        agentApi.deleteAgentTemplate(row.id, (res) => {
          if (res?.data?.code === 0) {
            this.$message.success(this.$t("agentTemplateManagement.deleteSuccess"));
            this.loadTemplateList();
          }
        });
      });
    },
    batchDeleteTemplate() {
      this.$confirm(this.$t("agentTemplateManagement.confirmBatchDelete", { count: this.selectedTemplates.length }), "Warning", { type: "warning" }).then(() => {
        const ids = this.selectedTemplates.map((t) => t.id);
        agentApi.batchDeleteAgentTemplate(ids, (res) => {
          if (res?.data?.code === 0) {
            this.$message.success(this.$t("agentTemplateManagement.batchDeleteSuccess"));
            this.selectedTemplates = [];
            this.isAllSelected = false;
            this.loadTemplateList();
          }
        });
      });
    },
    handlePageChange(page) {
      this.currentPage = page;
      this.loadTemplateList();
    },
    handlePageSizeChange(size) {
      this.pageSize = size;
      this.currentPage = 1;
      this.loadTemplateList();
    },
    handleSelectAll() {
      this.isAllSelected = !this.isAllSelected;
      this.templateList.forEach((row) => (row.selected = this.isAllSelected));
      this.selectedTemplates = this.isAllSelected ? [...this.templateList] : [];
    },
    handleRowSelectionChange() {
      this.selectedTemplates = this.templateList.filter((t) => t.selected);
      this.isAllSelected = this.templateList.length > 0 && this.selectedTemplates.length === this.templateList.length;
    },
  },
};
</script>
<style lang="scss" scoped>
@import "../styles/aurora-theme.scss";

/* --- Unique styles for AgentTemplateManagement --- */
.template-name-text { color: $accent-cyan; font-weight: bold; }
.serial-text { color: $text-muted; }
.action-cell { display: flex; justify-content: center; gap: 15px; }
</style>
