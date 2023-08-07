import {Collapse, Space} from "antd";
import type {CollapseProps} from "antd";
import ServiceConfigurationItem from "@/components/service/service-configuration-item";

const ServiceConfiguration = () => {
  const data1 = "export JAVA_HOME={{java64_home}}\n" +
    "export ZOOKEEPER_HOME={{zk_home}}\n" +
    "export ZOO_LOG_DIR={{zk_log_dir}}\n" +
    "export ZOOPIDFILE={{zk_pid_file}}\n" +
    "export SERVER_JVMFLAGS={{zk_server_heapsize}}\n" +
    "export JAVA=$JAVA_HOME/bin/java\n" +
    "export CLASSPATH=$CLASSPATH:/usr/share/zookeeper/*"

  const items: CollapseProps['items'] = [
    {
      key: '1',
      label: 'Advanced zoo.cfg',
      children:
        <div>
          <ServiceConfigurationItem label={"admin.serverPort"} type={"input"} data={"9393"}/>
          <ServiceConfigurationItem label={"autopurge.purgeInterval"} type={"input"} data={"24"}/>
          <ServiceConfigurationItem label={"zookeeper-env template"} type={"textarea"} data={data1}/>
        </div>,
    },
    {
      key: '2',
      label: 'Advanced zookeeper-env',
      children:
        <div>
          <ServiceConfigurationItem label={"admin.serverPort"} type={"input"} data={"9393"}/>
          <ServiceConfigurationItem label={"autopurge.purgeInterval"} type={"input"} data={"24"}/>
          <ServiceConfigurationItem label={"zookeeper-env template"} type={"textarea"} data={data1}/>
        </div>,
    },
    {
      key: '3',
      label: 'Advanced zookeeper-log4j',
      children:
        <div>
          <ServiceConfigurationItem label={"admin.serverPort"} type={"input"} data={"9393"}/>
          <ServiceConfigurationItem label={"autopurge.purgeInterval"} type={"input"} data={"24"}/>
          <ServiceConfigurationItem label={"zookeeper-env template"} type={"textarea"} data={data1}/>
        </div>
    },
  ];

  const onChange = (key: string | string[]) => {
    console.log(key);
  };

  return (
    <Collapse bordered={false} className={"bg-white"} items={items} onChange={onChange}/>
  )
}

export default ServiceConfiguration;