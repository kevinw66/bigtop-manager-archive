import React from "react";
import {Input, Result} from "antd";
import {LikeOutlined, SmileOutlined} from "@ant-design/icons";
import TextArea from "antd/es/input/TextArea";

const SetHosts = () => {
    return (
      <div className={"flex flex-col justify-start items-center content-center h-full"}>
          <div className={"text-2xl mb-4"}>Set Hosts</div>
          <TextArea rows={18} placeholder={"Enter a list of hosts, one per line."}/>
      </div>
    )
}

export default SetHosts;