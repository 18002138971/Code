# coding:utf-8

import sys
sys.path.append("utils")

import xlsxwriter
reload(sys)
sys.setdefaultencoding('utf8')

class AgingExcelTool:
    """输出信息至excel的工具类."""

    def __init__(self, file_name, file_dir):
        """AgingExcelTool."""
        #新建excel文件和sheet
        self._workbook = xlsxwriter.Workbook(file_dir + "/"+ file_name)
        self._worksheet = self._workbook.add_worksheet()
        #设置多行标题格式，在excel的顶部使用
        self._format_multi_lines_title = self._workbook.add_format()
        self._format_multi_lines_title.set_bg_color('EEEE00') #黄色调
        self._format_multi_lines_title.set_size(15)
        self._format_multi_lines_title.set_border(1)
        self._format_multi_lines_title.set_align('center')
        self._format_multi_lines_title.set_align('vcentre')
        self._format_multi_lines_title.set_bold()
        #设置单行标题格式
        self._format_single_line_title = self._workbook.add_format()
        self._format_single_line_title.set_border(1)
        self._format_single_line_title.set_bg_color('#cccccc') #灰色调
        self._format_single_line_title.set_align('center')
        self._format_single_line_title.set_bold()
        #设置文本格式
        self._format_text = self._workbook.add_format()
        self._format_text.set_border(1)
        self._format_text.set_align('center')
        #设置注解格式
        self._format_instructions = self._workbook.add_format()
        self._format_instructions.set_bg_color('EEEE00') #黄色调
        self._format_instructions.set_size(11)
        self._format_instructions.set_border(1)
        self._format_instructions.set_align('center')
        #设置表格列宽度
        self._worksheet.set_column('A:A',25)
        self._worksheet.set_column('B:B',20)
        self._worksheet.set_column('C:C',5)
        self._worksheet.set_column('D:D',25)
        self._worksheet.set_column('E:E',20)
        #设置开始写的行
        self._write_cursor = 0

    def close(self):
        self._workbook.close()

    def transfer_data_to_excel(self, aging_info):
        self._set_excel_title()
        self._transfer_statistics_info(aging_info)
        self._write_phone_memory_info_to_excel(aging_info)
        self._add_phone_used_memory_chart(aging_info)
        self._write_all_packages_memory_info_to_excel(aging_info)

    def _set_excel_title(self):
        """写excel总标题."""
        self._worksheet.merge_range(self._write_cursor, 0, self._write_cursor+2, 4, "老化测试内存监测报告", self._format_multi_lines_title)
        #写了3行，游标加3
        self._write_cursor = self._write_cursor + 3

    def _transfer_statistics_info(self, aging_info):
        """写统计信息至excel."""
        #写手机基本手机信息至excel
        buname = [u'手机型号',u'内部手机代号',u'MIUI版本',u'Android版本',u'序列号',u'处理器',u'编译模式',u'手机内存']
        self._worksheet.write_column('A4', buname, self._format_single_line_title)
        self._worksheet.write_string('B4', aging_info.model, self._format_text)
        self._worksheet.write_string('B5', aging_info.device, self._format_text)
        self._worksheet.write_string('B6', aging_info.miui_version, self._format_text)
        self._worksheet.write_string('B7', aging_info.android_version, self._format_text)
        self._worksheet.write_string('B8', aging_info.serial_num, self._format_text)
        self._worksheet.write_string('B9', aging_info.cpu_info, self._format_text)
        self._worksheet.write_string('B10', aging_info.compile_mode, self._format_text)
        self._worksheet.write_string('B11', aging_info.ram_size, self._format_text)

        buname = [u'统计次数',u'统计时间间隔（分钟）',u'package个数',u'手机已用内存初始值',u'手机已用内存最大值']
        self._worksheet.write_column('D4', buname, self._format_single_line_title)
        self._worksheet.write_string('E4', str(aging_info.statistics_times), self._format_text)
        self._worksheet.write_string('E5', str(aging_info.time_interval), self._format_text)
        self._worksheet.write_string('E6', str(aging_info.package_count), self._format_text)
        self._worksheet.write_string('E7', str(aging_info.origin_used_memory), self._format_text)
        self._worksheet.write_string('E8', str(aging_info.max_used_memory), self._format_text)

        #写了9行，游标加9
        self._write_cursor = self._write_cursor + 9

    def _write_phone_memory_info_to_excel(self, aging_info):
        #空两行
        self._write_cursor = self._write_cursor + 2
        self._worksheet.merge_range(self._write_cursor, 0, self._write_cursor+1, 3, u'手机内存使用情况一览表', self._format_multi_lines_title)
        self._write_cursor = self._write_cursor + 2
        self._worksheet.write_string('A'+str(self._write_cursor+1), u'统计轮次', self._format_single_line_title)
        self._worksheet.merge_range(self._write_cursor,1,self._write_cursor,2, u'手机已使用内存(MB)', self._format_single_line_title)
        self._worksheet.write_string('D'+str(self._write_cursor+1), u'手机剩余内存(MB)', self._format_single_line_title)
        self._write_cursor = self._write_cursor + 1
        size = len(aging_info.phone_used_memory_list)
        for i in range(size):
            self._worksheet.write_string('A'+str(self._write_cursor+i+1), "第" + str(i+1) + "轮", self._format_single_line_title)
            self._worksheet.merge_range(self._write_cursor+i,1,self._write_cursor+i,2, aging_info.phone_used_memory_list[i], self._format_text)
            self._worksheet.write_number('D'+str(self._write_cursor+i+1), aging_info.phone_free_memory_list[i], self._format_text)
        self._write_cursor = self._write_cursor + size

    def _add_phone_used_memory_chart(self ,aging_info):
        data_size = len(aging_info.phone_used_memory_list)
        phone_used_memory_chart = self._workbook.add_chart({'type':'line'})
        phone_used_memory_chart.add_series({
            'categories':'==Sheet1!$A$'+str(self._write_cursor-data_size+1)+':$A$' + str(self._write_cursor),
            'values':'=Sheet1!$B$'+str(self._write_cursor-data_size+1)+':$B$' + str(self._write_cursor),
            'name':'手机已用内存（单位:MB）',
            'line':{
                'color' : '#008573',
                'width':2,
            },
        })
        if data_size < 10:
            phone_used_memory_chart.set_size({'width': 600, 'height': 300})
        else:
            phone_used_memory_chart.set_size({'width': 800, 'height': 400})
        phone_used_memory_chart.set_title({'name':"手机已用内存趋势图"})
        phone_used_memory_chart.set_y_axis({'max':aging_info.max_used_memory,'min':0})
        phone_used_memory_chart.set_x_axis({'name':'轮次'})
        self._worksheet.insert_chart('E'+str(self._write_cursor-data_size -2),phone_used_memory_chart)

    def _write_all_packages_memory_info_to_excel(self, aging_info):
        for package in aging_info.package_info_list:
            self._write_package_info_to_excel(package)
            self._add_package_memory_chart(package)


    def _write_package_info_to_excel(self, package):
        """写列表信息至excel."""
        size = len(package.memory_list)
        #避免写入时覆盖了图表导致出错
        if size < 14:
            self._write_cursor = self._write_cursor + 14 - size
        else:
            #空两行
            self._write_cursor = self._write_cursor + 2
        self._worksheet.merge_range(self._write_cursor, 0, self._write_cursor+1,1, package.package_name, self._format_multi_lines_title)
        self._write_cursor = self._write_cursor + 2
        self._worksheet.write_string('A'+str(self._write_cursor+1), u'统计轮次', self._format_single_line_title)
        self._worksheet.write_string('B'+str(self._write_cursor+1), u'占用内存（MB）', self._format_single_line_title)
        self._write_cursor = self._write_cursor + 1
        for i in range(size):
            self._worksheet.write_string('A'+str(self._write_cursor+i+1), "第" + str(i+1) + "轮", self._format_single_line_title)
            self._worksheet.write_number('B'+str(self._write_cursor+i+1), package.memory_list[i], self._format_text)
        self._write_cursor = self._write_cursor + size
        self._worksheet.write_string('A'+str(self._write_cursor+1), u'平均值', self._format_single_line_title)
        self._worksheet.write_number('B'+str(self._write_cursor+1), package.average_memory_size, self._format_text)
        self._write_cursor = self._write_cursor + 1

    def _add_package_memory_chart(self, package):
        data_size = len(package.memory_list)
        package_memory_chart = self._workbook.add_chart({'type':'line'})
        package_memory_chart.add_series({
            'categories':'==Sheet1!$A$'+str(self._write_cursor-data_size)+':$A$' + str(self._write_cursor-1),
            'values':'=Sheet1!$B$'+str(self._write_cursor-data_size)+':$B$' + str(self._write_cursor-1),
            'name':'内存占用（单位:MB）',
            'line':{
                'color' : '#008573',
                'width':2,
            },
        })
        if data_size < 10:
            package_memory_chart.set_size({'width': 600, 'height': 300})
        else:
            package_memory_chart.set_size({'width': 800, 'height': 400})
        package_memory_chart.set_title({'name':"占用内存趋势图"})
        package_memory_chart.set_y_axis({'max':package.max_memory_size,'min':0})
        package_memory_chart.set_x_axis({'name':'轮次'})
        self._worksheet.insert_chart('E'+str(self._write_cursor-data_size -2),package_memory_chart)