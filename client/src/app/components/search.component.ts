import { Component, Input, OnChanges, OnInit, SimpleChange, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MarvelService } from '../marvel.service';
import { Character } from '../model';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {

  // for the search form, search field
  searchForm!: FormGroup
  characters: Character[] = []
  searchTerm!: string

  // for the pagination form, page selector field
  pageForm!: FormGroup
  noOfRecPerPage: number = 20

  // for the pagination display
  offsetCurrentIndex: number = 0
  pageNo: number = 1
  maxPage: number = 0




  constructor(private fb: FormBuilder, private marvelSvc: MarvelService) {}

  ngOnInit(): void {
      this.searchForm = this.createForm()
      this.pageForm = this.createPageForm()
  }


  // to get the value of the search field
  doSearch() {
    this.offsetCurrentIndex = 0
    this.pageNo = 1
    this.searchTerm = this.searchForm.get('searchTerm')?.value
    console.info('>>> searchForm: ngSubmit(): searchTerm: ', this.searchTerm)
    console.info(">>> searchForm: ngSubmit(): noOfRecPerPage: ", this.noOfRecPerPage)
    this.marvelSvc.getCharacters(this.searchTerm, this.noOfRecPerPage, this.offsetCurrentIndex) // calls the service to make http request to spring boot
      .then(result => { // get the result from the http request
        this.characters = result // since it is already a jsonarray string, type cast into a list, so it can be retured directly here
      })
      .catch(error => {
        console.info('>>> in error')
        console.error('>>> error: ', error)
      })
    this.searchForm.reset()
  }

  // to get the changed value of the page record selector field, how many records the user wants to display
  newRecPerPage() {
    // console.log("newRecPerPage")
    console.info(">>> newRecPerPage(): noOfRecPerPage: ", this.noOfRecPerPage)
    this.noOfRecPerPage = +this.pageForm.get("noOfRecPerPage")?.value; // use this + to cast as number
    console.info('>>> newRecPerPage(): noOfRecPerPage: ', this.noOfRecPerPage)
    this.marvelSvc.getCharacters(this.searchTerm, this.noOfRecPerPage, this.offsetCurrentIndex) // calls the service to make http request to spring boot
      .then(result => { // get the result from the http request
        this.characters = result // since it is already a jsonarray string, type cast into a list, so it can be retured directly here
      })
      .catch(error => {
        console.info('>>> in error')
        console.error('>>> error: ', error)
      })
  }

  // not required as the change is on the same component
  // listen to pagination changes and update the number of records to display
  // binding for the prev and next button
  // ngOnChanges(changes: SimpleChanges): void {
  //   // console.info('changes: ', changes)
  //   console.info('noOfRecPerPage:changes: ', changes['noOfRecPerPage'].currentValue)
  //   if(changes['noOfRecPerPage'].currentValue == null)
  //     this.noOfRecPerPage = 5;
  //   else
  //     this.noOfRecPerPage = changes['noOfRecPerPage'].currentValue;
    
  //   this.marvelSvc.getCharacters(this.searchTerm, this.noOfRecPerPage, this.offsetCurrentIndex) // calls the service to make http request to spring boot
  //     .then(result => { // get the result from the http request
  //       this.characters = result // since it is already a jsonarray string, type cast into a list, so it can be retured directly here
  //     })
  //     .catch(error => {
  //       console.info('>>> in error')
  //       console.error('>>> error: ', error)
  //     })
  // }

  nextPage() {
    this.pageNo++
    this.offsetCurrentIndex = this.offsetCurrentIndex + this.noOfRecPerPage
    console.info('>>> nextPage(): noOfRecPerPage: ', this.noOfRecPerPage)
    console.info('>>> nextPage(): offsetCurrentIndex: ', this.offsetCurrentIndex)
    this.marvelSvc.getCharacters(this.searchTerm, this.noOfRecPerPage, this.offsetCurrentIndex) // calls the service to make http request to spring boot
      .then(result => { // get the result from the http request
        this.characters = result // since it is already a jsonarray string, type cast into a list, so it can be retured directly here
      })
      .catch(error => {
        console.info('>>> in error')
        console.error('>>> error: ', error)
      })
  }

  previousPage() {
    this.pageNo--
    this.offsetCurrentIndex = this.offsetCurrentIndex - this.noOfRecPerPage
    this.marvelSvc.getCharacters(this.searchTerm, this.noOfRecPerPage, this.offsetCurrentIndex) // calls the service to make http request to spring boot
      .then(result => { // get the result from the http request
        this.characters = result // since it is already a jsonarray string, type cast into a list, so it can be retured directly here
      })
      .catch(error => {
        console.info('>>> in error')
        console.error('>>> error: ', error)
      })
  }


  // helper functions
  private createForm() {
    return this.fb.group({
      searchTerm: this.fb.control('', Validators.required)
    })
  }

  private createPageForm() {
    return this.fb.group({
      noOfRecPerPage: this.fb.control('20')
    })
  }

}
