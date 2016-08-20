public class Workspace {

    // Constants

    /**
     * Constant to indicate target user does not have sufficient privilege
     */
    private static final String INSUFFICIENT_PRIVILEGE_TARGET_USER = "TARGET_USER";
    /**
     * Constant to determine name of root directory name in workspace
     */
    private static final String DEFAULT_ROOT_DIRECTORY_NAME = "Workspace";

    // Table names
    static final String TABLE_NAME_FDS_PACKAGE_VERSION = "fds_package_version";
    static final String TABLE_NAME_FDS_PACKAGE = "fds_package";

    // Display names
    /**
     * Display name for name
     */
    private static final String DISP_NAME_NAME = "Name";
    /**
     * Display name for description
     */
    private static final String DISP_NAME_DESCRIPTION = "Description";
    /**
     * Display name for label
     */
    private static final String DISP_NAME_LABEL = "Tags";
    /**
     * Display name for Hide Workspace Activity
     */
    private static final String DISP_NAME_HIDE_WORKSPACE_ACTIVITY = "HideWorkspaceActivity";

    // Column names
    /**
     * Column name for package id
     */
    private static final String COL_NAME_PACKAGE_ID = "package_id";
    /**
     * Column name for name
     */
    private static final String COL_NAME_NAME = "name";
    /**
     * Column name for description
     */
    private static final String COL_NAME_DESCRIPTION = "description";
    /**
     * Column name for description
     */
    private static final String COL_NAME_LABEL = "label";
    /**
     * Column name for hide workspace activity
     */
    private static final String COL_NAME_HIDE_WORKSPACE_ACTIVITY = "hide_workspace_activity";
    /**
     * Column name for status
     */
    private static final String COL_NAME_STATUS = "status";
    /** Column name for folder id */
    // private static final String COL_NAME_FOLDER_ID = "folder_id";
    /**
     * Column name for latest version
     */
    private static final String COL_NAME_LATEST_VERSION = "latest_version";
    /**
     * Column name for latest published version
     */
    private static final String COL_NAME_LATEST_PUBLISHED_VERSION = "latest_published_version";
    /**
     * Column name for created by
     */
    private static final String COL_NAME_CREATED_BY = "created_by";
    /**
     * Column name for automatic package deletion date
     */
    private static final String COL_NAME_AUTO_DEL_DATE = "auto_del_date";
    /**
     * Column name for automatic package deletion reminder date
     */
    private static final String COL_NAME_AUTO_DEL_REMINDER_DATE = "auto_del_reminder_date";
    /**
     * Column name for automatic package deletion reminder sent date
     */
    private static final String COL_NAME_AUTO_DEL_REMINDER_SENT_DATE = "auto_del_reminder_sent_date";
    /**
     * Column name for date created
     */
    private static final String COL_NAME_DATE_CREATED = "date_created";
    /**
     * Column name for last updated by
     */
    private static final String COL_NAME_LAST_UPDATED_BY = "last_updated_by";
    /**
     * Column name for date last updated
     */
    private static final String COL_NAME_DATE_LAST_UPDATED = "date_last_updated";
    /**
     * Column name for last activity date
     */
    private static final String COL_NAME_LAST_ACTIVITY_DATE = "last_activity_date";
    /**
     * Column name for number of valid primary owners
     */
    private static final String COL_NAME_VALID_PO_COUNT = "valid_po_count";
    /**
     * Column name for indicating workspace
     */
    private static final String COL_NAME_IS_WORKSPACE = "is_workspace";
    /**
     * Column name for lock status
     */
    private static final String COL_NAME_IS_LOCKED = "is_locked";
    /**
     * Column name for locked by
     */
    private static final String COL_NAME_LOCKED_BY = "locked_by";

    /**
     * Enumeration for lock operation
     */
    public enum LOCK_OPERATION {
        LOCK, UNLOCK
    }

    ;

    public static enum EditWorkspaceType {
        EDIT_INFO, ADD_DOCUMENTS, EDIT_USERS
    }

    ;

    // Set up the log
    private static Logger log = Logger.getLogger(Workspace.class.getName());

    private WorkspaceVO workspaceVO;


    /**
     * Get all the associated information for the specified Workspace.
     *
     * @param conn      The connection to use
     * @param packageId The package id
     * @param lockRow   Flag to indicate if the package row should be locked
     * @param requester The requester
     * @throws ObjectNotFoundException        If the object could not be found.
     * @throws InsufficientPrivilegeException If the requester does not have sufficient privilege to get the
     *                                        object.
     * @throws SystemErrorException           If a system error is encountered.
     */
    Workspace(final Connection conn, final int packageId, final boolean lockRow, final Requester requester)
            throws ObjectNotFoundException, InsufficientPrivilegeException, SystemErrorException {
        // final String cn = "Package.Package(conn, packageId, lockRow, requester)"; // Log context name

        init(conn, packageId, lockRow, requester);
    }


    /**
     * Get all the associated information for the specified Workspace.
     *
     * @param packageId The package id
     * @param requester The requester
     * @throws ObjectNotFoundException        If the object could not be found.
     * @throws InsufficientPrivilegeException If the requester does not have sufficient privilege to get the
     *                                        object.
     * @throws SystemErrorException           If a system error is encountered.
     */
    Workspace(final int packageId, final Requester requester) throws ObjectNotFoundException,
            InsufficientPrivilegeException, SystemErrorException {
        final String cn = "Package.Package(packageId, requester)"; // Log context name

        Connection conn = null;
        try {
            conn = FDSDBUtil.getConnection(); // Get a connection to the database

            init(conn, packageId, false, requester); // Don't lock row

        } catch (CannotGetConnectionException e) {
            Util.logErrorAndThrowException(cn, "Could not get a database connection", e);
        } finally {
            // Close the connection (ignore any exceptions)
            try {
                DBUtil.closeConnection(conn, cn);
            } catch (SQLCloseException e) {
                ;
            }
        }
    }

    /**
     * Save workspace user settings given the <tt>SaveWorkspaceUserSettingsInput</tt> parameter.
     *
     * @param input The <tt>SaveWorkspaceUserSettingsInput</tt> object
     * @return <tt>SaveWorkspaceUserSettingsOutput</tt> indicating success or failure.
     */
    public static SaveWorkspaceUserSettingsOutput saveWorkspaceUserSettings(SaveWorkspaceUserSettingsInput input) {
        int rc = SaveWorkspaceUserSettingsOutput.RC_SUCCESS;

        SaveWorkspaceUserSettingsOutput output = null;
        try {
            ActionHelper ush = new SaveWorkspaceUserSettingsHelper(input);
            output = (SaveWorkspaceUserSettingsOutput) ush.execute();
        } catch (ValidationException e) {
            rc = SaveWorkspaceUserSettingsOutput.RC_ERR_SYSTEM_ERROR;
        } catch (ObjectNotFoundException e) {
            // rc = SaveWorkspaceUserSettingsOutput.RC_ERR_FOLDER_NOT_FOUND;
            rc = SaveWorkspaceUserSettingsOutput.RC_ERR_SYSTEM_ERROR;
        } catch (InsufficientPrivilegeException e) {
            rc = SaveWorkspaceUserSettingsOutput.RC_ERR_INSUFFICIENT_PRIVILEGE;
        } catch (SystemErrorException e) {
            rc = SaveWorkspaceUserSettingsOutput.RC_ERR_SYSTEM_ERROR;
        } finally {
            if (output == null) {
                output = new SaveWorkspaceUserSettingsOutput(SaveWorkspaceUserSettingsOutput.RC_ERR_SYSTEM_ERROR);
            }
        }
        return output;
    }

    static final class SaveWorkspaceUserSettingsHelper extends ActionHelper {

        // instance variables
        private SaveWorkspaceUserSettingsInput input;
        private Requester requester;
        private int packageId;
        private int userId;
        private Workspace w;


        public SaveWorkspaceUserSettingsHelper(SaveWorkspaceUserSettingsInput input) {
            this.input = input;
        }

        /**
         * Constructor.
         *
         * @param conn  The database connection to use.
         * @param input The input object.
         */
        SaveWorkspaceUserSettingsHelper(final Connection conn, SaveWorkspaceUserSettingsInput input) {
            super(conn);
            this.input = input;
        }

        @Override
        protected void initAndValidate(Connection conn) throws ObjectNotFoundException,
                InsufficientPrivilegeException, ValidationException, SystemErrorException {
            preValidate(conn);
            loadInternalObjects(conn);

        }


        private void preValidate(Connection conn) throws SystemErrorException {
            final String cn = "saveWorkspaceUserSettingsHelper.preValidate()"; // Log context name

            if (this.input == null || this.input.getRequester() == null) {
                Util.logErrorAndThrowException(cn,
                        "Parameter validation error: Input or Requester Object is Null.");
            } else if (this.input.getPackageId() == WorkspaceVO.DEFAULT_PACKAGE_ID) {
                Util.logErrorAndThrowException(cn, "PackageId is invalid.");
            } else if (this.input.getUserId() == UserVO.DEFAULT_USER_ID) {
                Util.logErrorAndThrowException(cn, "Parameter validation error: User not valid.");
            } else if (this.input.getSettingMap() == null) {
                Util.logErrorAndThrowException(cn,
                        "Parameter validation error: Notification settings map is Null.");
            }
        }


        private void loadInternalObjects(Connection conn) throws ObjectNotFoundException,
                InsufficientPrivilegeException, SystemErrorException {
            this.packageId = this.input.getPackageId();
            this.userId = this.input.getUserId();
            this.requester = this.input.getRequester();
            this.w = new Workspace(conn, packageId, true, requester); // lock workspace to avoid concurrency issues
        }


        @Override
        protected void checkPermissions(Connection conn) throws InsufficientPrivilegeException,
                SystemErrorException {
            final String cn = "saveWorkspaceUserSettingsHelper.checkPermissions()"; // Log context name
            String message = "";

            // check if workspace feature is enabled
            if (!InitProperty.getBooleanInitProperty(conn, InitPropertyVO.PROPERTY_WORKSPACE_ENABLE)) {
                message = "Secure workspace feature is not enabled";
                log.error(cn, message);
                throw new InsufficientPrivilegeException(message);
            }

            // Check If requester has one of the following role: SENDER, SENDER_RESTRICTED or RECIPIENT.
            if (!User.hasRole(conn, this.requester, Role.SENDER)
                    && !User.hasRole(conn, this.requester, Role.SENDER_RESTRICTED)
                    && !User.hasRole(conn, this.requester, Role.RECIPIENT)) {
                message += "Requester does not have any of the following roles: SENDER, SENDER_RESTRICTED, "
                        + "RECIPIENT. Requester Obj: " + this.requester;
            }
            // Check whether user is a ROOT or Viewer or Collaborator or Manager of the workspace.
            if (!w.isViewerHelper(conn, requester) && !w.isCollaboratorHelper(conn, requester)
                    && !w.isManagerHelper(conn, requester)) {
                message += "Requester is not a member(viewer or collaborator or manager) of the workspace: "
                        + this.packageId + ". Requester:" + this.requester;
            }
            if (!User.isRootUserHelper(requester) && !"".equals(message)) {
                log.info(cn, message);
                throw new InsufficientPrivilegeException(message);
            }
        }


        @Override
        protected Object executeHelper(Connection conn) throws SystemErrorException {
            final String cn = "saveWorkspaceUserSettingsHelper.executeHelper()"; // Log context name

            saveUserSettingsDBHelper(conn);

            return new SaveWorkspaceUserSettingsOutput(SaveWorkspaceUserSettingsOutput.RC_SUCCESS);
        }


        private void saveUserSettingsDBHelper(Connection conn) throws SystemErrorException {

            final String cn = "saveWorkspaceUserSettingsHelper.saveUserSettingsDBHelper()"; // Log context name

            Map<String, Boolean> inputMap = this.input.getSettingMap();
            String[] keys = new String[]{WorkspaceNotificationVO.EVENT_FILE_UPLOADED,
                    WorkspaceNotificationVO.EVENT_FILE_DOWNLOADED, WorkspaceNotificationVO.EVENT_FILE_UPDATED,
                    WorkspaceNotificationVO.EVENT_FILE_DELETED, WorkspaceNotificationVO.EVENT_FILE_UPDATED,
                    WorkspaceNotificationVO.EVENT_COMMENT_ADDED, WorkspaceNotificationVO.EVENT_DIR_UPDATED,
                    WorkspaceNotificationVO.EVENT_OTHER_CHANGES, WorkspaceNotificationVO.EVENT_USER_ADDED,
                    WorkspaceNotificationVO.EVENT_USER_DELETED, WorkspaceNotificationVO.EVENT_WORKSPACE_DELETED,
                    WorkspaceNotificationVO.EVENT_USER_SELF_REMOVE};
            boolean[] values = new boolean[]{true, true, true, true, true, true, true, true, true, true, true,
                    true};

            for (int i = 0; i < keys.length; i++) {
                if (inputMap.containsKey(keys[i])) {
                    values[i] = inputMap.get(keys[i]);
                }
            }

            String sqlStmt = "";
            try {
                /* Although we are passing boolean values in sql, but BqlUpdateStmt convert them to "Y" or "N" */
                for (int i = 0; i < keys.length; i++) {

					/*
					 * we will try to update first, assuming that update call will be more frequent. If update
					 * fails, we would then try to insert.
					 */
                    BqlUpdateStmt bus = new BqlUpdateStmt(conn);

                    bus.append("UPDATE fds_package_user_settings ");
                    bus.append("SET ");
                    bus.append("enabled=? ", values[i]);
                    bus.append("WHERE ( package_id=? AND user_id=? AND  notification_type=? ) ", new Object[]{
                            packageId, userId, keys[i]});

                    // log.debug(cn, bus.debugString());
                    sqlStmt = bus.debugString();

                    int rc = bus.execute();
                    // rc = 0 for no update which means no row exists, so we call insert
                    if (rc < 1) {
                        BqlUpdateStmt bus2 = new BqlUpdateStmt(conn);
                        bus2.append("INSERT INTO fds_package_user_settings( ");
                        bus2.append("  package_id, user_id, notification_type, enabled ");
                        bus2.append(") VALUES ( ");
                        bus2.append("  ?, ?, ?, ? ", new Object[]{packageId, userId, keys[i], values[i]});
                        bus2.append(") ");

                        // log.debug(cn, bus.debugString());
                        sqlStmt = bus2.debugString();

                        bus2.execute();
                    }

                }

            } catch (SQLException e) {
                Util.logErrorAndThrowException(cn, "Unexpected SQL error while running: " + sqlStmt.toString(), e);
            }
        }
    }
}