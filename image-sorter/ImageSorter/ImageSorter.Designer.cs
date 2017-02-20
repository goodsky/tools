namespace ImageSorter
{
    partial class ImageSorter
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.DoneButton = new System.Windows.Forms.Button();
            this.SorterLabel = new System.Windows.Forms.Label();
            this.StatusLabel = new System.Windows.Forms.Label();
            this.UndoButton = new System.Windows.Forms.Button();
            this.SorterListView = new System.Windows.Forms.ListView();
            this.OutputGroupBox = new System.Windows.Forms.GroupBox();
            this.OutputPrefixTextBox = new System.Windows.Forms.TextBox();
            this.OutputPathPrefixLabel = new System.Windows.Forms.Label();
            this.OutputFolderButton = new System.Windows.Forms.Button();
            this.OutputFolderLabel = new System.Windows.Forms.Label();
            this.OutputFolderTextBox = new System.Windows.Forms.TextBox();
            this.OutputGroupBox.SuspendLayout();
            this.SuspendLayout();
            // 
            // DoneButton
            // 
            this.DoneButton.Location = new System.Drawing.Point(482, 331);
            this.DoneButton.Name = "DoneButton";
            this.DoneButton.Size = new System.Drawing.Size(193, 29);
            this.DoneButton.TabIndex = 2;
            this.DoneButton.Text = "Done";
            this.DoneButton.UseVisualStyleBackColor = true;
            this.DoneButton.Click += new System.EventHandler(this.DoneButton_Click);
            // 
            // SorterLabel
            // 
            this.SorterLabel.AutoSize = true;
            this.SorterLabel.Location = new System.Drawing.Point(12, 9);
            this.SorterLabel.Name = "SorterLabel";
            this.SorterLabel.Size = new System.Drawing.Size(231, 13);
            this.SorterLabel.TabIndex = 4;
            this.SorterLabel.Text = "Select the next image one at a time to sort them";
            // 
            // StatusLabel
            // 
            this.StatusLabel.AutoSize = true;
            this.StatusLabel.Location = new System.Drawing.Point(214, 339);
            this.StatusLabel.Name = "StatusLabel";
            this.StatusLabel.Size = new System.Drawing.Size(103, 13);
            this.StatusLabel.TabIndex = 5;
            this.StatusLabel.Text = "0 of X images sorted";
            // 
            // UndoButton
            // 
            this.UndoButton.Location = new System.Drawing.Point(15, 331);
            this.UndoButton.Name = "UndoButton";
            this.UndoButton.Size = new System.Drawing.Size(193, 29);
            this.UndoButton.TabIndex = 6;
            this.UndoButton.Text = "Undo";
            this.UndoButton.UseVisualStyleBackColor = true;
            this.UndoButton.Click += new System.EventHandler(this.UndoButton_Click);
            // 
            // SorterListView
            // 
            this.SorterListView.Alignment = System.Windows.Forms.ListViewAlignment.Left;
            this.SorterListView.Location = new System.Drawing.Point(15, 25);
            this.SorterListView.MultiSelect = false;
            this.SorterListView.Name = "SorterListView";
            this.SorterListView.Size = new System.Drawing.Size(660, 300);
            this.SorterListView.TabIndex = 7;
            this.SorterListView.UseCompatibleStateImageBehavior = false;
            // 
            // OutputGroupBox
            // 
            this.OutputGroupBox.Controls.Add(this.OutputPrefixTextBox);
            this.OutputGroupBox.Controls.Add(this.OutputPathPrefixLabel);
            this.OutputGroupBox.Controls.Add(this.OutputFolderButton);
            this.OutputGroupBox.Controls.Add(this.OutputFolderLabel);
            this.OutputGroupBox.Controls.Add(this.OutputFolderTextBox);
            this.OutputGroupBox.Location = new System.Drawing.Point(17, 368);
            this.OutputGroupBox.Name = "OutputGroupBox";
            this.OutputGroupBox.Size = new System.Drawing.Size(657, 52);
            this.OutputGroupBox.TabIndex = 8;
            this.OutputGroupBox.TabStop = false;
            this.OutputGroupBox.Text = "Output Settings";
            // 
            // OutputPrefixTextBox
            // 
            this.OutputPrefixTextBox.Location = new System.Drawing.Point(551, 19);
            this.OutputPrefixTextBox.Name = "OutputPrefixTextBox";
            this.OutputPrefixTextBox.Size = new System.Drawing.Size(100, 20);
            this.OutputPrefixTextBox.TabIndex = 4;
            this.OutputPrefixTextBox.Text = "img";
            // 
            // OutputPathPrefixLabel
            // 
            this.OutputPathPrefixLabel.AutoSize = true;
            this.OutputPathPrefixLabel.Location = new System.Drawing.Point(474, 22);
            this.OutputPathPrefixLabel.Name = "OutputPathPrefixLabel";
            this.OutputPathPrefixLabel.Size = new System.Drawing.Size(71, 13);
            this.OutputPathPrefixLabel.TabIndex = 3;
            this.OutputPathPrefixLabel.Text = "Output Prefix:";
            // 
            // OutputFolderButton
            // 
            this.OutputFolderButton.Location = new System.Drawing.Point(418, 17);
            this.OutputFolderButton.Name = "OutputFolderButton";
            this.OutputFolderButton.Size = new System.Drawing.Size(32, 23);
            this.OutputFolderButton.TabIndex = 2;
            this.OutputFolderButton.Text = "...";
            this.OutputFolderButton.UseVisualStyleBackColor = true;
            this.OutputFolderButton.Click += new System.EventHandler(this.OutputFolderButton_Click);
            // 
            // OutputFolderLabel
            // 
            this.OutputFolderLabel.AutoSize = true;
            this.OutputFolderLabel.Location = new System.Drawing.Point(6, 22);
            this.OutputFolderLabel.Name = "OutputFolderLabel";
            this.OutputFolderLabel.Size = new System.Drawing.Size(74, 13);
            this.OutputFolderLabel.TabIndex = 1;
            this.OutputFolderLabel.Text = "Output Folder:";
            // 
            // OutputFolderTextBox
            // 
            this.OutputFolderTextBox.Location = new System.Drawing.Point(86, 19);
            this.OutputFolderTextBox.Name = "OutputFolderTextBox";
            this.OutputFolderTextBox.Size = new System.Drawing.Size(326, 20);
            this.OutputFolderTextBox.TabIndex = 0;
            // 
            // ImageSorter
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(687, 432);
            this.Controls.Add(this.OutputGroupBox);
            this.Controls.Add(this.SorterListView);
            this.Controls.Add(this.UndoButton);
            this.Controls.Add(this.StatusLabel);
            this.Controls.Add(this.SorterLabel);
            this.Controls.Add(this.DoneButton);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedToolWindow;
            this.Name = "ImageSorter";
            this.Text = "ImageSorter";
            this.OutputGroupBox.ResumeLayout(false);
            this.OutputGroupBox.PerformLayout();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion
        private System.Windows.Forms.Button DoneButton;
        private System.Windows.Forms.Label SorterLabel;
        private System.Windows.Forms.Label StatusLabel;
        private System.Windows.Forms.Button UndoButton;
        private System.Windows.Forms.ListView SorterListView;
        private System.Windows.Forms.GroupBox OutputGroupBox;
        private System.Windows.Forms.Label OutputFolderLabel;
        private System.Windows.Forms.TextBox OutputFolderTextBox;
        private System.Windows.Forms.TextBox OutputPrefixTextBox;
        private System.Windows.Forms.Label OutputPathPrefixLabel;
        private System.Windows.Forms.Button OutputFolderButton;
    }
}