class ReducedRowEchelonForm:
    def __init__(self, matrix):
        self.matrix = matrix

    def swap_rows(self, i, j):
        self.matrix[i], self.matrix[j] = self.matrix[j], self.matrix[i]

    def scale_row(self, i, scale):
        self.matrix[i] = [scale * x for x in self.matrix[i]]

    def add_scaled_row(self, source_row, target_row, scale):
        self.matrix[target_row] = [
            x + scale * y
            for x, y in zip(self.matrix[target_row], self.matrix[source_row])
        ]

    def to_reduced_row_echelon_form(self):
        lead = 0
        row_count = len(self.matrix)
        column_count = len(self.matrix[0])
        for r in range(row_count):
            if lead >= column_count:
                return
            i = r
            while self.matrix[i][lead] == 0:
                i += 1
                if i == row_count:
                    i = r
                    lead += 1
                    if column_count == lead:
                        return
            self.swap_rows(i, r)
            if self.matrix[r][lead] != 0:
                self.scale_row(r, 1.0 / self.matrix[r][lead])
            for i in range(row_count):
                if i != r:
                    self.add_scaled_row(r, i, -self.matrix[i][lead])
            lead += 1

    def print_matrix(self):
        for row in self.matrix:
            print(row)

    def __str__(self):
        return f"RREF로 변경된 행렬은 {self.matrix} 입니다."


# # Example Usage
if __name__ == "__main__":
    matrix = [[1, 2, 3], [0, 1, 2], [2, 3, 4]]
    rref = ReducedRowEchelonForm(matrix)
    rref.to_reduced_row_echelon_form()
    # print(rref)
    rref.print_matrix()
