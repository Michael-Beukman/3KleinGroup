from PyPDF2 import PdfFileWriter, PdfFileReader, PdfFileMerger
from fpdf import FPDF
import os


import glob



def get_file_reader(page, tot_pages, name):
    class PDFPageNum(FPDF):
        def __init__(self, page_num, tot_pages, name):
            super(PDFPageNum, self).__init__("L", format=(30,200))
            self.page_num = page_num
            self.total_pages = tot_pages
            self.name = name

        def footer(self):
            self.set_font('Arial', 'I', 12)
            # $this->SetY(-15);
            # self.set_y(-15)
            # self.set_x(-1)
            self.cell(0, 10, f"{name}", 1, 1, align='C')
            
            # self.cell(self.h-30, self.w - 100, f"{self.name}", 1, 1, align='C')

    new_pdf = PDFPageNum(page, tot_pages, name)
    new_pdf.add_page("L")
    
    new_pdf.output("temp.pdf")
    new_pdf = PdfFileReader('temp.pdf')
    return new_pdf

def add_page_numbers_merge(output_name):
    pdfs = glob.glob("**/*.pdf") + glob.glob("release_1_UML/user stories/pdfs/*.pdf")
    print(pdfs)
    num_pages=len(pdfs)
    output = PdfFileWriter()
    for i, file in enumerate(pdfs):
        existing_pdf = PdfFileReader(file)
        new_pdf = get_file_reader(i+1, num_pages+1, os.path.basename(file)) # one with footer
        
        page = existing_pdf.getPage(0)
        # page.mergePage(new_pdf.getPage(0))
        output.addPage(new_pdf.getPage(0))
        output.addPage(page)

    if os.path.exists('temp.pdf'):
        os.remove('temp.pdf')

    output_stream = open(output_name, "wb")
    output.write(output_stream)
    output_stream.close()

add_page_numbers_merge('out.pdf')


# from PyPDF2 import PdfFileMerger


# merger = PdfFileMerger()

# for pdf in pdfs:
#     merger.append(pdf)

# merger.write("result.pdf")
# merger.close()
