import React from "react";
import {Content} from "antd/es/layout/layout";

const AdminContent = ({children}: {
  children: React.ReactNode
}) => {
  return (
    <Content className="mt-6 mx-4">
      <div className="p-6 h-full bg-white">{children}</div>
    </Content>
  )
}

export default AdminContent;