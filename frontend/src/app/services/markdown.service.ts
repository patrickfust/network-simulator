import {Injectable} from '@angular/core';
import mermaid from 'mermaid';
import {DomSanitizer, SafeHtml} from '@angular/platform-browser';

@Injectable({
  providedIn: 'root',
})
export class MarkdownService {
  private showdown: any;

  constructor(private sanitizer: DomSanitizer) {
    this.addTocExtension().then();
    this.addFontAwesomeExtension().then();
  }

  private async loadShowdown() {
    if (!this.showdown) {
      const showdownModule = await import('showdown');
      this.showdown = showdownModule.default; // Access the default export
    }
  }

  private async addTocExtension() {
    await this.loadShowdown();
    this.showdown.extension('toc', () => {
      return [
        {
          type: 'output',
          filter: (text: string) => {
            const headings = text.match(/<h[1-6].*?>(.*?)<\/h[1-6]>/g);
            if (!headings) return text;

            let previousLevel = 1;
            const toc = headings
              .map((heading) => {
                const level = parseInt(heading.charAt(2), 10);
                const idMatch = heading.match(/id="(.*?)"/);
                const content = heading.replace(/<.*?>/g, '');
                const id = idMatch ? idMatch[1] : content.replace(/\s+/g, '').toLowerCase();
                var tocEntry = '';
                var tocExit = '';
                if (level > previousLevel) {
                  tocEntry = "<ul>".repeat(level - previousLevel);
                  previousLevel = level;
                } else if (level < previousLevel) {
                  tocEntry = "</ul>".repeat(previousLevel - level);
                  previousLevel = level;
                }
                return `${tocEntry}<li class="toc-level-${level}"><a href="home#${id}">${content}</a></li>`;
              })
              .join('');

            const finalToc = toc + '</ul>'.repeat(previousLevel - 1);
            const tocHtml = `<h1>Table of contents</h1><ul class="toc">${finalToc}</ul>`;
            return tocHtml + text.replace(/<h([1-6])(.*?)>(.*?)<\/h\1>/g, (match, level, attrs, content) => {
              const idMatch = attrs.match(/id="(.*?)"/);
              const id = idMatch ? idMatch[1] : content.replace(/\s+/g, '').toLowerCase();
              const updatedAttrs = attrs.replace(/id=".*?"/, '').trim();
              return `<h${level} id="${id}" ${updatedAttrs}>${content}</h${level}>`;
            });
          },
        },
      ];
    });
  }

  async convert(markdown: string, toc: boolean): Promise<SafeHtml> {
    return this.sanitizer.bypassSecurityTrustHtml(await this.makeHtmlWithMermaid(markdown, toc));
  }

  private async makeHtmlWithMermaid(markdown: string, toc: boolean) {
    await this.loadShowdown();
    const converter = new this.showdown.Converter({
      tables: true,
      extensions: toc ? ['toc', 'fontawesome'] : ['fontawesome'],
    });
    let html = converter.makeHtml(markdown);

    // Render Mermaid diagrams
    const mermaidRegex = /<code class="language-mermaid">([\s\S]*?)<\/code>/g;
    html = html.replace(mermaidRegex, (_: string, code: string) => {
      try {
        const mermaidId = `mermaid-${Math.random().toString(36).substr(2, 9)}`;
        mermaid.parse(code).then(); // Validate Mermaid syntax
        return `<div class="mermaid" id="${mermaidId}">${code}</div>`;
      } catch (e) {
        console.error('Mermaid syntax error:', e);
        return `<pre>${code}</pre>`;
      }
    });
    return html;
  }

  private async addFontAwesomeExtension() {
    await this.loadShowdown();
    this.showdown.extension('fontawesome', () => {
      return [
        {
          type: 'lang',
          regex: /:fa-([a-z0-9-]+):/g,
          replace: '<i class="fas fa-$1"></i>'
        },
        {
          type: 'lang',
          regex: /:far-([a-z0-9-]+):/g,
          replace: '<i class="far fa-$1"></i>'
        },
        {
          type: 'lang',
          regex: /:fab-([a-z0-9-]+):/g,
          replace: '<i class="fab fa-$1"></i>'
        }
      ];
    });
  }

}
