import {AfterContentInit, ChangeDetectorRef, Component, ElementRef, Input, OnInit} from '@angular/core';
import {MarkdownService} from '../../services/markdown.service';
import mermaid from 'mermaid';
import {SafeHtml} from '@angular/platform-browser';

@Component({
  selector: 'markdown',
  templateUrl: './markdown.html',
  styleUrl: './markdown.scss',
  preserveWhitespaces: true,
})
export class Markdown implements OnInit, AfterContentInit {

  @Input() markdownContent?: string;
  @Input() markdownFile?: string;
  @Input() toc?: boolean;

  htmlContent: SafeHtml = '';

  constructor(
    private markdownService: MarkdownService,
    private hostElement: ElementRef,
    private cdr: ChangeDetectorRef) {
  }

  ngOnInit(): void {
    if (this.markdownContent) {
      this.markdownService.convert(this.markdownContent, !!this.toc).then((html) => {
        this.htmlContent = html;
        this.renderMermaidDiagrams();
      });
    } else if (this.markdownFile) {
      this.loadMarkdownFromFile().then();
    }
  }

  private async loadMarkdownFromFile(): Promise<void> {
    if (this.markdownFile) {
      console.log(`Loading markdown from file: ${this.markdownFile}`);
      const response = await fetch(this.markdownFile);
      this.markdownContent = await response.text();
      this.htmlContent = await this.markdownService.convert(this.markdownContent, !!this.toc);
      this.cdr.markForCheck(); // Trigger change detection
      this.renderMermaidDiagrams();
    }
  }

  ngAfterContentInit(): void {
    if (!this.markdownContent && !this.markdownFile) {
      const rawContent: string = (this.hostElement.nativeElement.textContent || this.hostElement.nativeElement.innerText).trim();
      this.markdownService.convert(rawContent, !!this.toc).then((html) => {
        this.htmlContent = html;
        this.renderMermaidDiagrams();
      });
    }
  }

  private renderMermaidDiagrams(): void {
    setTimeout(() => {
      mermaid.initialize({startOnLoad: false});
      mermaid.run({querySelector: '.mermaid'}).then();
    });
  }

}
