INSERT INTO bigtop_manager.cluster (id, create_time, update_time, cache_dir, cluster_desc, cluster_name, cluster_type, packages, repo_template, root, user_group, stack_id) VALUES (1, '2023-08-09 13:56:20.032673', '2023-08-09 13:56:20.032673', '/opt/bigtop-manager-agent/cache', null, 'c1', 1, '[ "curl", "wget" ]', '[{{repo_id}}]
name={{repo_id}}
{% if mirror_list %}mirrorlist={{mirror_list}}{% else %}baseurl={{base_url}}{% endif %}
path=/
enabled=1
gpgcheck=0
', '/opt/bigtop', 'hadoop', 1);
