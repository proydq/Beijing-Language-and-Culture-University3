-- auto-generated definition
create table tb_terminal
(
    id                   varchar(40)  not null,
    cstm_id              varchar(40)  not null,
    terminal_id          varchar(64)  null,
    extra_id1            varchar(64)  null,
    extra_id2            varchar(64)  null,
    terminal_name        varchar(64)  null,
    terminal_desc        varchar(255) null,
    cat_id               varchar(40)  null,
    create_time          datetime     null,
    create_user          varchar(64)  null,
    program_config       varchar(64)  null,
    terminal_config      varchar(64)  null,
    online_state         int          null comment '0:离线 1:开机 2:关机',
    logon_time           datetime     null,
    logoff_time          datetime     null,
    last_alive_time      datetime     null,
    board_type           varchar(64)  null,
    ipaddr               varchar(15)  null,
    display_width        int          null,
    display_height       int          null,
    volume               int          null,
    network_type         int          null,
    app_version          varchar(64)  null comment '1-2-3 ； 1表示daemon 2表示touch程序 3第三方程序',
    firmware_version     varchar(255) null,
    total_space          bigint       null,
    free_space           bigint       null,
    program_version      varchar(255) null comment '节目的版本',
    device_model_id      varchar(40)  null,
    classroom_name       varchar(64)  null,
    class_name           varchar(255) null,
    entrance_open_status int(1)       null comment '终端门禁常闭常开状态 1常开 2常闭',
    entrance_type        int(1)       null comment '进出类型 1进门 2出门',
    primary key (id, cstm_id),
    constraint cstm_ter_index
        unique (cstm_id, terminal_id)
)
    row_format = DYNAMIC;

create index id
    on tb_terminal (id);

