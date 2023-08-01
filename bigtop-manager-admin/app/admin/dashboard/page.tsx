"use client"

import {Card, Divider, Progress, Statistic, Table} from "antd";
import {ReactNode, RefObject, useEffect, useRef, useState} from "react";
import * as echarts from "echarts";
import {ColumnsType} from "antd/es/table";

const App: React.FC = () => {

  let resourceChart = useRef<HTMLDivElement>(null);
  let diskChart = useRef<HTMLDivElement>(null);
  let [props, setProps] = useState({title: "Resource"});
  let [diskProps, setDiskProps] = useState({title: "Disk"});

  useEffect(() => {
    const timeout = setTimeout(() => {
      const chart = echarts.init(resourceChart.current);
      let option = {
        title: {
          text: props.title ? props.title : "暂无数据",
        },
        legend: {
          data: ['Core Usage(%)', 'Memory Usage(%)']
        },
        grid: {
          left: '60px',
          width: '75%',
        },
        xAxis: {
          type: 'category',
          data: [
            "10:00",
            "11:00",
            "12:00",
            "13:00",
            "14:00",
            "15:00",
            "16:00",
          ],
        },
        yAxis: {
          type: 'value',
          scale: true,
          name: 'Percentage',
          axisLabel: {
            formatter: '{value} %'
          }
        },
        series: [{
          name: 'Core Usage(%)',
          data: [10, 10, 90, 30, 10, 90, 90],
          type: 'line',
          smooth: true,
        }, {
          name: 'Memory Usage(%)',
          data: [22, 19, 88, 66, 5, 90, 75],
          type: 'line',
          smooth: true,
        }],
        tooltip: {
          show: true,
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            axis: 'auto'
          },
          showContent: true,
        }
      };

      chart.setOption(option);
    });

    return () => clearTimeout(timeout);
  }, [props])

  useEffect(() => {
    const timeout = setTimeout(() => {
      const chart = echarts.init(diskChart.current);
      let option = {
        title: {
          text: diskProps.title ? diskProps.title : "暂无数据",
        },
        legend: {
          data: ['Percentage(%), Increment(MB)']
        },
        grid: {
          left: '60px',
          width: '75%',
        },
        xAxis: {
          type: 'category',
          data: [
            "2023/08/13",
            "2023/08/14",
            "2023/08/15",
            "2023/08/16",
            "2023/08/17",
            "2023/08/18",
            "2023/08/19",
          ],
        },
        yAxis: [{
          type: 'value',
          scale: true,
          name: 'Increment',
          min: 0,
          max: 1000,
          interval: 100,
          axisLabel: {
            formatter: '{value} MB'
          }
        }, {
          type: 'value',
          scale: true,
          name: 'Percentage',
          max: 100,
          min: 0,
          interval: 10,
          axisLabel: {
            formatter: '{value} %'
          }
        }],
        series: [{
          name: 'Disk Usage(%)',
          data: [20, 35, 40, 65, 75, 90, 97],
          type: 'line',
          smooth: true,
          yAxisIndex: 1,
        }, {
          name: 'Increment(MB)',
          data: [100, 800, 500, 233, 420, 155, 333],
          type: 'bar',
        }],
        tooltip: {
          show: true,
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            axis: 'auto'
          },
          showContent: true,
        }
      };

      chart.setOption(option);
    }, 100);

    return () => clearTimeout(timeout);
  }, [diskProps])

  interface DataType {
    key: string;
    name: string;
    ip: string;
    cores: string;
    memory: string;
    disk: number;
    load: string;
  }

  const dataSource: DataType[] = [
    {
      key: '192.168.0.1',
      name: 'bigtop-manager-server',
      ip: '192.168.0.1',
      cores: '4',
      memory: '16GB',
      disk: 90,
      load: '2%',
    },
    {
      key: '192.168.0.2',
      name: 'bigtop-manager-agent-01',
      ip: '192.168.0.2',
      cores: '4',
      memory: '16GB',
      disk: 30,
      load: '2%',
    },
    {
      key: '192.168.0.3',
      name: 'bigtop-manager-agent-02',
      ip: '192.168.0.3',
      cores: '4',
      memory: '16GB',
      disk: 70,
      load: '2%',
    },
    {
      key: '192.168.0.4',
      name: 'bigtop-manager-agent-03',
      ip: '192.168.0.4',
      cores: '4',
      memory: '16GB',
      disk: 15,
      load: '2%',
    },
  ];

  const columns: ColumnsType<DataType> = [
    {
      title: 'Name',
      dataIndex: 'name',
      key: 'name',
      align: 'center',
    },
    {
      title: 'IP Address',
      dataIndex: 'ip',
      key: 'ip',
      align: 'center',
    },
    {
      title: 'Cores',
      dataIndex: 'cores',
      key: 'cores',
      align: 'center',
    },
    {
      title: 'Memory',
      dataIndex: 'memory',
      key: 'memory',
      align: 'center',
    },
    {
      title: 'Disk Usage',
      dataIndex: 'disk',
      key: 'disk',
      align: 'center',
      render: (text: number) => <Progress strokeColor={'#52c41a'} percent={text} size="small" />,
    },
    {
      title: 'Load Avg',
      dataIndex: 'load',
      key: 'load',
      align: 'center',
    },
  ];

  return (
    <>
      <div className={"flex justify-between items-center"}>
        <Card className={"w-1/5"} bodyStyle={{textAlign: "center"}}>
          <Statistic title="Hosts" value={4}/>
          <div className={"text-xs text-gray-400 mt-1"}>Healthy: 3</div>
        </Card>
        <Card className={"w-1/5"} bodyStyle={{textAlign: "center"}}>
          <Statistic title="Cores" value={12} suffix={" / " + 16}/>
          <div className={"text-xs text-gray-400 mt-1"}>75%</div>
        </Card>
        <Card className={"w-1/5"} bodyStyle={{textAlign: "center"}}>
          <Statistic title="Memory" value={123} suffix={" / " + 256 + " G"}/>
          <div className={"text-xs text-gray-400 mt-1"}>48.05%</div>
        </Card>
        <Card className={"w-1/5"} bodyStyle={{textAlign: "center"}}>
          <Statistic title="Disks" value={2} suffix={" / " + 5 + " T"}/>
          <div className={"text-xs text-gray-400 mt-1"}>40%</div>
        </Card>
      </div>
      <Divider dashed={true}/>
      <div className={"flex justify-between items-center"}>
        <div ref={resourceChart} className={"w-[45%] h-96"}/>
        <div ref={diskChart} className={"w-[45%] h-96"}/>
      </div>
      <Divider dashed={true}/>
      <Table dataSource={dataSource} columns={columns} pagination={false} />
    </>
  )
}

export default App;