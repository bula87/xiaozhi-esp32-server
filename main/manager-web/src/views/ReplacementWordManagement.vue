<template>
  <div class="page-container">
    <div class="operation-bar">
      <h2 class="page-title">{{ $t('replacementWordManagement.pageTitle') }}</h2>
      <div class="right-operations">
        </div>
    </div>

    <div class="main-wrapper">
      <div class="content-panel">
        <div class="content-area">
          <el-card class="params-card" shadow="never">
            <el-table
              ref="paramsTable"
              :data="paramsList"
              class="transparent-table"
              v-loading="loading"
              height="100%"
              :element-loading-text="$t('common.loading')"
              element-loading-spinner="el-icon-loading"
              element-loading-background="rgba(11, 15, 25, 0.8)"
            >
              <el-table-column align="center" width="80">
                <template slot="header">
                  <span class="selection-header-text">{{ $t('modelConfig.select') }}</span>
                </template>
                <template slot-scope="scope">
                  <el-checkbox v-model="scope.row.selected" @change="handleCheckboxChange(scope.row)"></el-checkbox>
                </template>
              </el-table-column>

              <el-table-column :label="$t('replacementWordManagement.fileName')" prop="fileName" align="center">
                <template slot-scope="scope">
                  <span class="file-name-text">{{ scope.row.fileName }}</span>
                </template>
              </el-table-column>

              <el-table-column :label="$t('replacementWordManagement.replacementWordCount')" prop="wordCount" align="center" width="120" />

              <el-table-column :label="$t('replacementWordManagement.replacementWordContent')" align="center">
                <template slot-scope="scope">
                  <el-tooltip placement="top" effect="dark" popper-class="aurora-tooltip">
                    <div slot="content" class="replace-word-content">
                      <el-tag v-for="(item, index) in scope.row.content" :key="index" size="mini" class="aurora-tag">{{ item }}</el-tag>
                    </div>
                    <span class="content-preview">{{ formatContent(scope.row.content) }}</span>
                  </el-tooltip>
                </template>
              </el-table-column>

              <el-table-column :label="$t('replacementWordManagement.createTime')" prop="createdAt" align="center" width="180">
                <template slot-scope="scope">
                  <span class="time-text">{{ scope.row.createdAt }}</span>
                </template>
              </el-table-column>

              <el-table-column :label="$t('replacementWordManagement.operation')" align="center" width="220">
                <template slot-scope="scope">
                  <el-button type="text" size="mini" @click="handleEdit(scope.row)">{{ $t('replacementWordManagement.edit') }}</el-button>
                  <el-button type="text" size="mini" class="download-btn" @click="handleDownload(scope.row)">{{ $t('replacementWordManagement.download') }}</el-button>
                  <el-button type="text" size="mini" class="delete-btn" @click="handleDelete(scope.row)">{{ $t('replacementWordManagement.delete') }}</el-button>
                </template>
              </el-table-column>
            </el-table>

            <div class="table_bottom">
              <div class="ctrl_btn">
                <el-button size="mini" type="primary" @click="handleSelectAll">
                  {{ allSelected ? $t('user.deselectAll') : $t('user.selectAll') }}
                </el-button>
                <el-button size="mini" type="success" @click="handleAdd">{{ $t('replacementWordManagement.addFile') }}</el-button>
                <el-button size="mini" type="danger" :disabled="!hasAnySelected" @click="handleBatchDelete">
                  {{ $t('replacementWordManagement.batchDelete') }}
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
                  @current-change="fetchFileList"
                />
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <div class="footer-container">
      <VersionFooter/>
    </div>

    <ReplacementWordDialog
      ref="paramDialog"
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      :form="dialogForm"
      @submit="handleSubmit"
      @cancel="dialogVisible = false"
    />
  </div>
</template>

<script>
import Api from "@/apis/api";
import VersionFooter from "@/components/VersionFooter.vue";
import ReplacementWordDialog from "@/components/ReplacementWordDialog.vue";

export default {
  name: "ReplacementWordManagement",
  components: { VersionFooter, ReplacementWordDialog },
  data() {
    return {
      paramsList: [],
      selectedRows: new Set(),
      currentPage: 1,
      loading: false,
      pageSize: 10,
      pageSizeOptions: [10, 20, 50, 100],
      total: 0,
      dialogVisible: false,
      dialogTitle: '',
      dialogForm: {},
    };
  },
  created() {
    this.fetchFileList();
  },
  computed: {
    hasAnySelected() {
      return this.selectedRows.size > 0;
    },
    allSelected() {
      return this.paramsList.length > 0 && this.paramsList.every(row => this.selectedRows.has(row.id));
    },
  },
  methods: {
    formatContent(content) {
      if (!content) return '';
      return Array.isArray(content) ? content.join(', ') : content;
    },
    handlePageSizeChange(val) {
      this.pageSize = val;
      this.currentPage = 1;
      this.fetchFileList();
    },
    handleCheckboxChange(row) {
      if (row.selected) this.selectedRows.add(row.id);
      else this.selectedRows.delete(row.id);
    },
    handleSelectAll() {
      const targetState = !this.allSelected;
      this.paramsList.forEach(row => {
        this.$set(row, 'selected', targetState);
        if (targetState) this.selectedRows.add(row.id);
        else this.selectedRows.delete(row.id);
      });
    },
    fetchFileList() {
      this.loading = true;
      Api.correctWord.getFileList({
        page: this.currentPage,
        pageSize: this.pageSize
      }, ({ data }) => {
        this.loading = false;
        if (data.code === 0) {
          this.paramsList = (data.data.list || []).map(row => ({
            ...row,
            selected: this.selectedRows.has(row.id)
          }));
          this.total = data.data.total || 0;
        }
      });
    },
    handleAdd() {
      this.dialogForm = { id: undefined, fileName: '', content: '' };
      this.dialogTitle = this.$t('replacementWordManagement.addFile');
      this.dialogVisible = true;
    },
    handleEdit(row) {
      this.dialogForm = { id: row.id, fileName: row.fileName, content: row.content || '' };
      this.dialogTitle = this.$t('replacementWordManagement.edit');
      this.dialogVisible = true;
    },
    handleDownload(row) {
      Api.correctWord.downloadFile(row.id, (res) => {
        const url = window.URL.createObjectURL(new Blob([res.data]));
        const link = document.createElement('a');
        link.href = url;
        link.download = `${row.fileName}.txt`;
        link.click();
        window.URL.revokeObjectURL(url);
      });
    },
    handleDelete(row) {
      this.$confirm(this.$t('replacementWordManagement.confirmDelete'), "Warning", { type: 'warning' }).then(() => {
        Api.correctWord.deleteFile(row.id, () => {
          this.$message.success(this.$t('common.deleteSuccess'));
          this.selectedRows.delete(row.id);
          this.fetchFileList();
        });
      });
    },
    handleBatchDelete() {
      const ids = Array.from(this.selectedRows);
      this.$confirm(this.$t('replacementWordManagement.confirmBatchDelete', { count: ids.length }), "Warning", { type: 'warning' }).then(() => {
        Api.correctWord.batchDeleteFile(ids, () => {
          this.$message.success(this.$t('common.deleteSuccess'));
          this.selectedRows.clear();
          this.fetchFileList();
        });
      });
    },
    handleSubmit(formData) {
      const apiMethod = formData.id ? Api.correctWord.updateFile : Api.correctWord.addFile;
      apiMethod(formData, ({ data }) => {
        if (data.code === 0) {
          this.$message.success(this.$t('replacementWordManagement.saveSuccess'));
          this.dialogVisible = false;
          this.fetchFileList();
        }
        if (this.$refs.paramDialog) this.$refs.paramDialog.resetSaving();
      });
    }
  },
};
</script>
<style lang="scss" scoped>
@import "../styles/aurora-theme.scss";

/* --- Unique styles for ReplacementWordManagement --- */
.word-text { color: $accent-cyan; font-weight: bold; }
</style>
