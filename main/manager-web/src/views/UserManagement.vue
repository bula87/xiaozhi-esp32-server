<template>
  <div class="page-container">
    <div class="operation-bar">
      <h2 class="page-title">{{ $t("header.userManagement") }}</h2>
      <div class="right-operations">
        <el-input
          v-model="searchPhone"
          :placeholder="$t('user.searchPhone')"
          class="search-input"
          clearable
          @keyup.enter.native="handleSearch"
        />
        <el-button class="btn-search" @click="handleSearch">
          {{ $t("user.search") }}
        </el-button>
      </div>
    </div>

    <div class="main-wrapper">
      <div class="content-panel">
        <div class="content-area">
          <el-card class="user-card" shadow="never">
            <el-table
              ref="userTable"
              :data="userList"
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
                  <el-checkbox v-model="scope.row.selected"></el-checkbox>
                </template>
              </el-table-column>

              <el-table-column :label="$t('user.userid')" prop="userid" align="center" width="100">
                <template slot-scope="scope">
                  <span class="id-text">{{ scope.row.userid }}</span>
                </template>
              </el-table-column>

              <el-table-column :label="$t('user.mobile')" prop="mobile" align="center" />
              
              <el-table-column :label="$t('user.deviceCount')" prop="deviceCount" align="center" width="120">
                <template slot-scope="scope">
                  <span class="count-text">{{ scope.row.deviceCount }}</span>
                </template>
              </el-table-column>

              <el-table-column :label="$t('user.createDate')" prop="createDate" align="center" width="180">
                <template slot-scope="scope">
                  <span class="time-text">{{ scope.row.createDate }}</span>
                </template>
              </el-table-column>

              <el-table-column :label="$t('user.status')" prop="status" align="center" width="120">
                <template slot-scope="scope">
                  <el-tag v-if="scope.row.status === 1" type="success" size="mini" effect="dark" class="aurora-tag">
                    {{ $t("user.normal") }}
                  </el-tag>
                  <el-tag v-else type="danger" size="mini" effect="dark" class="aurora-tag">
                    {{ $t("user.disabled") }}
                  </el-tag>
                </template>
              </el-table-column>

              <el-table-column :label="$t('modelConfig.action')" align="center" width="320">
                <template slot-scope="scope">
                  <el-button type="text" size="mini" @click="resetPassword(scope.row)">
                    {{ $t("user.resetPassword") }}
                  </el-button>
                  <el-button
                    type="text"
                    size="mini"
                    class="status-btn"
                    @click="handleChangeStatus(scope.row, scope.row.status === 1 ? 0 : 1)"
                  >
                    {{ scope.row.status === 1 ? $t("user.disableAccount") : $t("user.enableAccount") }}
                  </el-button>
                  <el-button type="text" size="mini" class="delete-btn" @click="deleteUser(scope.row)">
                    {{ $t("user.deleteUser") }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <div class="table_bottom">
              <div class="ctrl_btn">
                <el-button type="primary" size="mini" @click="handleSelectAll">
                  {{ isAllSelected ? $t("user.deselectAll") : $t("user.selectAll") }}
                </el-button>
                <el-button type="success" size="mini" icon="el-icon-circle-check" @click="batchEnable">
                  {{ $t("user.enable") }}
                </el-button>
                <el-button type="warning" size="mini" @click="batchDisable">
                  <i class="el-icon-remove-outline" style="margin-right: 4px;"></i>
                  {{ $t("user.disable") }}
                </el-button>
                <el-button type="danger" size="mini" icon="el-icon-delete" @click="batchDelete">
                  {{ $t("user.delete") }}
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
                  @current-change="fetchUsers"
                />
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <view-password-dialog :visible.sync="showViewPassword" :password="currentPassword" />

    <div class="footer-container">
      <version-footer />
    </div>
  </div>
</template>

<script>
import Api from "@/apis/api";
import VersionFooter from "@/components/VersionFooter.vue";
import ViewPasswordDialog from "@/components/ViewPasswordDialog.vue";

export default {
  name: 'UserManagment',
  components: { ViewPasswordDialog, VersionFooter },
  data() {
    return {
      showViewPassword: false,
      currentPassword: "",
      searchPhone: "",
      userList: [],
      pageSizeOptions: [10, 20, 50, 100],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      isAllSelected: false,
      loading: false,
    };
  },
  created() {
    this.fetchUsers();
  },
  methods: {
    handlePageSizeChange(val) {
      this.pageSize = val;
      this.currentPage = 1;
      this.fetchUsers();
    },
    fetchUsers() {
      this.loading = true;
      Api.admin.getUserList({
        page: this.currentPage,
        limit: this.pageSize,
        mobile: this.searchPhone,
      }, ({ data }) => {
        this.loading = false;
        if (data.code === 0) {
          this.userList = data.data.list.map(item => ({ ...item, selected: false }));
          this.total = data.data.total;
        }
      });
    },
    handleSearch() {
      this.currentPage = 1;
      this.fetchUsers();
    },
    handleSelectAll() {
      this.isAllSelected = !this.isAllSelected;
      this.userList.forEach(row => (row.selected = this.isAllSelected));
    },
    resetPassword(row) {
      this.$confirm(this.$t("user.confirmResetPassword"), this.$t("common.warning"), { type: "warning" }).then(() => {
        Api.admin.resetUserPassword(row.userid, ({ data }) => {
          if (data.code === 0) {
            this.$alert(`${this.$t("user.resetPasswordSuccess")}\n\n${this.$t("user.generatedPassword")}: ${data.data}`, this.$t("common.success"));
            this.fetchUsers();
          }
        });
      });
    },
    handleChangeStatus(row, status) {
      const users = Array.isArray(row) ? row : [row];
      const actionText = status === 0 ? this.$t("user.disable") : this.$t("user.enable");
      this.$confirm(this.$t("user.confirmStatusChange", { action: actionText, count: users.length }), this.$t("common.warning"), { type: "warning" }).then(() => {
        const userIds = users.map(u => u.userid);
        Api.user.changeUserStatus(status, userIds, ({ data }) => {
          if (data.code === 0) {
            this.$message.success(this.$t("user.statusChangeSuccess", { action: actionText, count: users.length }));
            this.fetchUsers();
          }
        });
      });
    },
    deleteUser(row) {
      this.$confirm(this.$t("user.confirmDeleteUser"), this.$t("common.warning"), { type: "warning" }).then(() => {
        Api.admin.deleteUser(row.userid, ({ data }) => {
          if (data.code === 0) {
            this.$message.success(this.$t("user.deleteUserSuccess"));
            this.fetchUsers();
          }
        });
      });
    },
    batchDelete() {
      const selected = this.userList.filter(u => u.selected);
      if (selected.length === 0) return this.$message.warning(this.$t("user.selectUsersFirst"));
      this.$confirm(this.$t("user.confirmDeleteSelected", { count: selected.length }), this.$t("common.warning"), { type: "warning" }).then(async () => {
        const promises = selected.map(u => new Promise(res => Api.admin.deleteUser(u.userid, res)));
        await Promise.all(promises);
        this.$message.success(this.$t("common.success"));
        this.fetchUsers();
      });
    },
    batchEnable() { this.handleChangeStatus(this.userList.filter(u => u.selected), 1); },
    batchDisable() { this.handleChangeStatus(this.userList.filter(u => u.selected), 0); },
    headerCellClassName({ columnIndex }) { return columnIndex === 0 ? "custom-selection-header" : ""; }
  }
};
</script>
<style lang="scss" scoped>
@import "../styles/aurora-theme.scss";

/* --- Unique styles for UserManagement --- */
.username-text { color: $accent-cyan; font-weight: bold; }
</style>
